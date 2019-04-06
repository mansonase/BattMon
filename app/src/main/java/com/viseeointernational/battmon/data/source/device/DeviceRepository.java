package com.viseeointernational.battmon.data.source.device;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.viseeointernational.battmon.data.constant.ConnectionType;
import com.viseeointernational.battmon.data.constant.StateType;
import com.viseeointernational.battmon.data.entity.Cranking;
import com.viseeointernational.battmon.data.entity.CrankingValue;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.data.entity.Trip;
import com.viseeointernational.battmon.data.entity.Voltage;
import com.viseeointernational.battmon.data.source.android.ble.BleEvent;
import com.viseeointernational.battmon.data.source.android.ble.BleService;
import com.viseeointernational.battmon.data.source.base.database.CrankingDao;
import com.viseeointernational.battmon.data.source.base.database.CrankingValueDao;
import com.viseeointernational.battmon.data.source.base.database.DeviceDao;
import com.viseeointernational.battmon.data.source.base.database.TripDao;
import com.viseeointernational.battmon.data.source.base.database.VoltageDao;
import com.viseeointernational.battmon.util.MathUtil;
import com.viseeointernational.battmon.util.StringUtil;
import com.viseeointernational.battmon.util.ValueUtil;
import com.viseeointernational.battmon.view.notification.Notifications;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DeviceRepository implements DeviceSource {

    private static final String TAG = DeviceRepository.class.getSimpleName();

    private DeviceDao deviceDao;
    private TripDao tripDao;
    private VoltageDao voltageDao;
    private CrankingDao crankingDao;
    private CrankingValueDao crankingValueDao;
    private BleService bleService;
    private Notifications notifications;

    private Map<String, Device> pairedDevices = new LinkedHashMap<>();

    @Inject
    public DeviceRepository(DeviceDao deviceDao, TripDao tripDao, VoltageDao voltageDao,
                            CrankingDao crankingDao, CrankingValueDao crankingValueDao,
                            BleService bleService, Notifications notifications) {
        this.deviceDao = deviceDao;
        this.tripDao = tripDao;
        this.voltageDao = voltageDao;
        this.crankingDao = crankingDao;
        this.crankingValueDao = crankingValueDao;
        this.bleService = bleService;
        this.notifications = notifications;
        EventBus.getDefault().register(this);
    }

    /**********************************************自动重连****************************************************/

    private Disposable autoDisposable;

    private void startAutoConnect() {
        stopAutoConnect();
        autoDisposable = Observable.interval(0, 20, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.d(TAG, "检测是否需要重连");
                        for (Map.Entry<String, Device> entry : pairedDevices.entrySet()) {
                            if (entry.getValue().connectionState == ConnectionType.DISCONNECTED) {
                                bleService.search(10);
                                break;
                            }
                        }
                    }
                });
    }

    private void stopAutoConnect() {
        if (autoDisposable != null && !autoDisposable.isDisposed()) {
            autoDisposable.dispose();
            autoDisposable = null;
        }
    }

    private void handleAutoReconnect(String address) {
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device != null && device.connectionState == ConnectionType.DISCONNECTED) {
                device.connectionState = ConnectionType.CONNECTING;
                bleService.connect(address, true);
                Log.d(TAG, "搜索到设备 执行重连");
            }
        }
    }

    @Override
    public boolean isBleEnable() {
        return bleService.isBleAvailable();
    }

    @Override
    public void onAppExit() {
        if (pairedDevices.size() == 0 || !bleService.isBleAvailable()) {
            stopAutoConnect();
            EventBus.getDefault().unregister(this);
            bleService.stopSelf();
        }
    }

    @Override
    public void init() {
        if (pairedDevices.size() == 0) {
            List<Device> devices = deviceDao.getPairedDevices();
            for (int i = 0; i < devices.size(); i++) {
                Device device = devices.get(i);
                device.voltage = voltageDao.getLashVoltage(device.address);
                device.cranking = crankingDao.getLashCranking(device.address);
                pairedDevices.put(device.address, device);
            }
        }
        startAutoConnect();
    }

    private void saveDeviceToDatabase(final Device device) {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer integer) {
                        deviceDao.insert(device);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public Device getDevice(@NonNull String address) {
        if (pairedDevices.containsKey(address)) {
            return pairedDevices.get(address);
        }
        return null;
    }

    @Override
    public List<Device> getDevices(String search) {
        if (TextUtils.isEmpty(search)) {
            return new ArrayList<>(pairedDevices.values());
        }
        List<Device> devices = new ArrayList<>();
        for (Map.Entry<String, Device> entry : pairedDevices.entrySet()) {
            Device device = entry.getValue();
            if (device.name.contains(search)) {
                devices.add(device);
            }
        }
        return devices;
    }

    @Override
    public int getDevicesCount() {
        return pairedDevices.size();
    }

    /**********************************************获取信息***************************************************/

    @Override
    public List<Voltage> getVoltages(@NonNull String address, long from, long to) {
        return voltageDao.getVoltages(address, from, to);
    }

    @Override
    public float getAvgVoltage(@NonNull String address, long from, long to) {
        return voltageDao.getAvgVoltageByState(address, from, to, StateType.VOLTAGE_DYING, StateType.VOLTAGE_LOW,
                StateType.VOLTAGE_GOOD);
    }

    @Override
    public List<Voltage> getAbnormalChargings(@NonNull String address, long from, long to) {
        return voltageDao.getVoltagesByState(address, from, to, StateType.OVER_CHARGING);
    }

    @Override
    public Cranking getLastCranking(@NonNull String address) {
        return crankingDao.getLashCranking(address);
    }

    @Override
    public List<Cranking> getCrankings(@NonNull String address, long from, long to) {
        List<Cranking> crankings = crankingDao.getCrankings(address, from, to);
        for (int i = 0; i < crankings.size(); i++) {
            Cranking cranking = crankings.get(i);
            cranking.value = crankingValueDao.getCrankingValues(address, cranking.startTime);
        }
        return crankings;
    }

    @Override
    public List<Trip> getTripsAndDetail(@NonNull String address, long from, long to) {
        List<Trip> trips = tripDao.getTrips(address, from, to);
        for (int i = 0; i < trips.size(); i++) {
            Trip trip = trips.get(i);
            trip.voltage = voltageDao.getLashVoltageByState(address, trip.startTime, trip.endTime,
                    StateType.VOLTAGE_DYING, StateType.VOLTAGE_LOW, StateType.VOLTAGE_GOOD);
            trip.cranking = crankingDao.getLashCrankingByTime(address, trip.startTime, trip.endTime);
            trip.charging = voltageDao.getLashVoltageByState(address, trip.startTime, trip.endTime,
                    StateType.CHARGING, StateType.OVER_CHARGING);
        }
        return trips;
    }

    @Override
    public List<Trip> getTrips(@NonNull String address, long from, long to) {
        return tripDao.getTrips(address, from, to);
    }

    /**********************************************监听voltage***************************************************/

    private VoltageListener voltageListener;

    private void handleVoltageChangeListen(Voltage voltage) {
        if (voltageListener != null) {
            voltageListener.onValueReceived(voltage);
        }
    }

    @Override
    public void setVoltageListener(@Nullable VoltageListener listener) {
        voltageListener = listener;
    }

    /**********************************************监听cranking***************************************************/

    private CrankingListener crankingListener;

    private void handleCrankingChangeListen(Cranking cranking) {
        if (crankingListener != null) {
            crankingListener.onValueReceived(cranking);
        }
    }

    @Override
    public void setCrankingListener(@Nullable CrankingListener listener) {
        crankingListener = listener;
    }

    /**********************************************监听连接状态***************************************************/

    private ConnectionChangeListener connectionChangeListener;

    private void handleConnectionChangeListen(String address, boolean isConnected) {
        if (connectionChangeListener != null) {
            connectionChangeListener.onConnectionChange(address, isConnected);
        }
    }

    @Override
    public void setConnectionChangeListener(@Nullable ConnectionChangeListener listener) {
        connectionChangeListener = listener;
    }

    /**********************************************搜索功能****************************************************/

    private SearchCallback searchCallback;

    private void handleSearchDeviceFound(String address, String name, int rssi) {
        if (searchCallback != null && !pairedDevices.containsKey(address) && name != null && name.startsWith("BM")) {
            Device device = new Device(address);
            device.name = name;
            device.rssi = rssi;
            searchCallback.onDeviceFound(device);
        }
    }

    private void handleSearchFinish() {
        if (searchCallback != null) {
            searchCallback.onFinish();
            searchCallback = null;
        }
    }

    @Override
    public void search(int seconds, @NonNull final SearchCallback callback) {
        if (!bleService.isBleAvailable()) {
            callback.onBleNotAvailable();
            return;
        }
        searchCallback = callback;
        bleService.search(seconds);
    }

    @Override
    public void stopSearch() {
        searchCallback = null;
        bleService.stopSearch();
    }

    /**********************************************主动连接****************************************************/

    private ConnectionCallback connectionCallback;
    private String connectCallbackAddress;

    private void handleConnectionDisconnected(String address) {
        if (connectionCallback != null && address.equals(connectCallbackAddress)) {
            connectionCallback.onDisconnected();
            connectionCallback = null;
        }
    }

    private void handleConnectionConnected(String address) {
        if (connectionCallback != null && address.equals(connectCallbackAddress)) {
            connectionCallback.onConnected();
            connectionCallback = null;
        }
    }

    @Override
    public void connect(@NonNull String address, @NonNull ConnectionCallback callback) {
        if (!bleService.isBleAvailable()) {
            callback.onBleNotAvailable();
            return;
        }
        connectCallbackAddress = address;
        connectionCallback = callback;
        bleService.connect(address, true);
    }

    @Override
    public void delete(@NonNull String address) {
        bleService.write(address, new byte[]{(byte) 0xa9});// 取消配对
        deviceDao.deleteByAddress(address);
    }

    @Override
    public void saveName(@NonNull final String address, @NonNull final String name) {
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device != null) {
                device.name = name;
                saveDeviceToDatabase(device);
            }
        }
    }

    @Override
    public void saveHeader(@NonNull final String address, final String imagePath) {
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device != null) {
                device.imagePath = imagePath;
                saveDeviceToDatabase(device);
            }
        }
    }

    @Override
    public void enableAbnormalNotification(@NonNull final String address, final boolean enable) {
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device != null) {
                device.enableAbnormalNotification = enable;
                saveDeviceToDatabase(device);
            }
        }
    }

    /**********************************************获取版本信息****************************************************/

    private GetVersionCallback getVersionCallback;
    private OperateTimer getVersionTimer;
    private String tempGetVersionAddress;

    private void handleA6(final String address, byte[] data) {
        if (getVersionCallback != null && address.equals(tempGetVersionAddress)) {
            getVersionTimer.stopCount();
            String name = new String(data, 5, 8, StandardCharsets.UTF_8);
            getVersionCallback.onSuccessful(data[4], name);
            getVersionCallback = null;
        }
    }

    @Override
    public void getVersion(@NonNull String address, @NonNull final GetVersionCallback callback) {
        if (!bleService.isBleAvailable()) {
            callback.onBleNotAvailable();
            return;
        }
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device == null) {
                callback.onDeviceNotAvailable();
                return;
            }
            if (!(device.connectionState == ConnectionType.CONNECTED)) {
                callback.onDeviceDisconnected();
                return;
            }
            tempGetVersionAddress = address;
            getVersionCallback = callback;
            bleService.write(address, new byte[]{(byte) 0xa6});
            getVersionTimer = new OperateTimer(new OperateTimer.Callback() {
                @Override
                public void onTimeOut() {
                    callback.onTimeOut();
                }
            });
            getVersionTimer.startCount();
            return;
        }
        callback.onDeviceNotAvailable();
    }

    /**********************************************usb立刻断电或开启****************************************************/

    private BleCallback enableUsbPowerOffCallback;
    private OperateTimer enableUsbPowerOffTimer;
    private String tempEnableUsbPowerOffAddress;
    private boolean tempEnableUsbPowerOff;

    private void handleAB(String address, byte[] data) {
        if (enableUsbPowerOffCallback != null && address.equals(tempEnableUsbPowerOffAddress)) {
            enableUsbPowerOffTimer.stopCount();
            Device device = pairedDevices.get(address);
            if (device != null) {
                device.enableUsbPowerOff = (data[4] == (byte) 0x00);
                saveDeviceToDatabase(device);
                if (device.enableUsbPowerOff == tempEnableUsbPowerOff) {
                    enableUsbPowerOffCallback.onSuccessful();
                } else {
                    enableUsbPowerOffCallback.onFailed();
                }
                enableUsbPowerOffCallback = null;
            }
        }
    }

    @Override
    public void enableUsbPowerOff(@NonNull final String address, final boolean enable, @NonNull final BleCallback callback) {
        if (!bleService.isBleAvailable()) {
            callback.onBleNotAvailable();
            return;
        }
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device == null) {
                callback.onDeviceNotAvailable();
                return;
            }
            if (!(device.connectionState == ConnectionType.CONNECTED)) {
                callback.onDeviceDisconnected();
                return;
            }
            tempEnableUsbPowerOffAddress = address;
            enableUsbPowerOffCallback = callback;
            tempEnableUsbPowerOff = enable;
            if (enable) {
                bleService.write(address, new byte[]{(byte) 0xab, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
            } else {
                bleService.write(address, new byte[]{(byte) 0xab, (byte) 0x01,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
            }
            enableUsbPowerOffTimer = new OperateTimer(new OperateTimer.Callback() {
                @Override
                public void onTimeOut() {
                    callback.onTimeOut();
                }
            });
            enableUsbPowerOffTimer.startCount();
            return;
        }
        callback.onDeviceNotAvailable();
    }

    /**********************************************usb延时断电功能****************************************************/

    private BleCallback delayUsbPowerOffCallback;
    private OperateTimer delayUsbPowerOffTimer;
    private String tempDelayUsbPowerOffAddress;
    private long tempStartTime;
    private long tempDelayTime;

    private void handleA8(String address, byte[] data) {
        if (delayUsbPowerOffCallback != null && address.equals(tempDelayUsbPowerOffAddress)) {
            delayUsbPowerOffTimer.stopCount();
            Device device = pairedDevices.get(address);
            if (device != null) {
                if ((tempDelayTime == -1 && data[6] == (byte) 0x11) || (tempDelayTime != -1 && data[6] == (byte) 0x22)) {
                    device.usbPowerOffDelayTime = tempDelayTime;
                    device.usbPowerOffStartTime = tempStartTime;
                    delayUsbPowerOffCallback.onSuccessful();
                } else {
                    device.usbPowerOffDelayTime = -1;
                    delayUsbPowerOffCallback.onFailed();
                }
                saveDeviceToDatabase(device);
                delayUsbPowerOffCallback = null;
            }
        }
    }

    @Override
    public void delayUsbPowerOff(@NonNull String address, long startTime, long delayTime, @NonNull final BleCallback callback) {
        if (!bleService.isBleAvailable()) {
            callback.onBleNotAvailable();
            return;
        }
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device == null) {
                callback.onDeviceNotAvailable();
                return;
            }
            if (!(device.connectionState == ConnectionType.CONNECTED)) {
                callback.onDeviceDisconnected();
                return;
            }
            tempDelayUsbPowerOffAddress = address;
            delayUsbPowerOffCallback = callback;
            tempStartTime = startTime;
            tempDelayTime = delayTime;
            if (delayTime == -1) {
                bleService.write(address, new byte[]{(byte) 0xa8, (byte) 0x00, (byte) 0x03, (byte) 0x11, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
            } else {
                int seconds = (int) (delayTime / 1000);
                bleService.write(address, new byte[]{(byte) 0xa8, (byte) 0x00, (byte) 0x03, (byte) 0x22, ValueUtil.getH(seconds), ValueUtil.getL(seconds),
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
            }
            delayUsbPowerOffTimer = new OperateTimer(new OperateTimer.Callback() {
                @Override
                public void onTimeOut() {
                    callback.onTimeOut();
                }
            });
            delayUsbPowerOffTimer.startCount();
            return;
        }
        callback.onDeviceNotAvailable();
    }

    /**********************************************recognize功能****************************************************/

    private BleCallback recognizeCallback;
    private OperateTimer recognizeTimer;
    private String tempRecognizeAddress;

    private void handleAA(final String address) {
        if (recognizeCallback != null && address.equals(tempRecognizeAddress)) {
            recognizeTimer.stopCount();
            recognizeCallback.onSuccessful();
            recognizeCallback = null;
        }
    }

    @Override
    public void recognize(@NonNull String address, @NonNull final BleCallback callback) {
        if (!bleService.isBleAvailable()) {
            callback.onBleNotAvailable();
            return;
        }
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device == null) {
                callback.onDeviceNotAvailable();
                return;
            }
            if (!(device.connectionState == ConnectionType.CONNECTED)) {
                callback.onDeviceDisconnected();
                return;
            }
            tempRecognizeAddress = address;
            recognizeCallback = callback;
            bleService.write(address, new byte[]{(byte) 0xaa});
            recognizeTimer = new OperateTimer(new OperateTimer.Callback() {
                @Override
                public void onTimeOut() {
                    callback.onTimeOut();
                }
            });
            recognizeTimer.startCount();
            return;
        }
        callback.onDeviceNotAvailable();
    }

    /**********************************************unpair***************************************************/

    private void deleteDeviceFromDatabase(final String address) {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer integer) {
                        deviceDao.deleteByAddress(address);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
        pairedDevices.remove(address);
    }

    @Override
    public void unpair(@NonNull final String address, boolean force, @NonNull final BleCallback callback) {
        if (force) {
            bleService.write(address, new byte[]{(byte) 0xa9});// 取消配对
            deleteDeviceFromDatabase(address);
            callback.onSuccessful();
            return;
        }
        if (!bleService.isBleAvailable()) {
            callback.onBleNotAvailable();
            return;
        }
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device == null) {
                callback.onDeviceNotAvailable();
                return;
            }
            if (!(device.connectionState == ConnectionType.CONNECTED)) {
                callback.onDeviceDisconnected();
                return;
            }
            bleService.write(address, new byte[]{(byte) 0xa9});
            OperateTimer unpairTimer = new OperateTimer(new OperateTimer.Callback() {
                @Override
                public void onTimeOut() {
                    deleteDeviceFromDatabase(address);
                    callback.onSuccessful();
                }
            });
            unpairTimer.startCount();
            return;
        }
        callback.onDeviceNotAvailable();
    }

    /**********************************************设置电压校正*************************************************** */

    private void handleF0(String address, byte[] data) {
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device != null) {
                device.calH = data[4];
                device.calL = data[5];
                saveDeviceToDatabase(device);
            }
        }
    }

    @Override
    public void setCalibration(@NonNull String address, byte calH, byte calL) {
        if (!bleService.isBleAvailable()) {
            return;
        }
        bleService.write(address, new byte[]{(byte) 0xf0, calH, calL});
    }

    /**********************************************设置电压***************************************************/

    private void handleA7(String address, byte[] data) {
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device != null) {
                device.chgOverH = data[4];
                device.chgOverL = data[5];
                device.chgH = data[6];
                device.chgL = data[7];
                device.idleLowH = data[8];
                device.idleLowL = data[9];
                device.triggerH = data[10];
                device.triggerL = data[11];
                device.crankLowH = data[12];
                device.crankLowL = data[13];
                saveDeviceToDatabase(device);
            }
        }
    }

    @Override
    public void setThresholdValue(@NonNull final String address, byte chgOverH, byte chgOverL, byte chgH, byte chgL, byte idleLowH, byte idleLowL,
                                  byte triggerH, byte triggerL, byte crankLowH, byte crankLowL) {
        if (!bleService.isBleAvailable()) {
            return;
        }
        bleService.write(address, new byte[]{(byte) 0xa7, chgOverH, chgOverL, chgH, chgL, idleLowH, idleLowL, triggerH, triggerL, crankLowH, crankLowL});
    }

    /********************************************gatt已连接回调*****************************************************/

    private void handleGattConnected(String address) {
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device != null) {
                byte[] b2 = new byte[5];
                b2[0] = (byte) 0xb2;
                b2[1] = StringUtil.hexString2byte(device.pairId.substring(0, 2));
                b2[2] = StringUtil.hexString2byte(device.pairId.substring(2, 4));
                b2[3] = StringUtil.hexString2byte(device.pairId.substring(4, 6));
                b2[4] = StringUtil.hexString2byte(device.pairId.substring(6, 8));
                bleService.write(address, b2);// 连线
            }
        } else {
            bleService.write(address, new byte[]{(byte) 0xb0});// 配对
        }
    }

    /************************************************gatt断开连接回调*************************************************/

    private void handleGattDisconnected(String address) {
        if (pairedDevices.containsKey(address)) {
            Device device = pairedDevices.get(address);
            if (device != null) {
                if (device.connectionState == ConnectionType.CONNECTED) {
                    // todo 播通知
                }
                device.connectionState = ConnectionType.DISCONNECTED;
            }
        }
    }

    /************************************************配对回调*************************************************/

    private byte[] pairIds = new byte[4];// 缓存pair id

    private void handleB0(String address, byte[] data) {
        System.arraycopy(data, 4, pairIds, 0, pairIds.length);// 拷贝
        byte[] b1 = new byte[5];
        b1[0] = (byte) 0xb1;
        System.arraycopy(pairIds, 0, b1, 1, 4);
        bleService.write(address, b1);// 确认配对
    }

    /************************************************确认配对回调*************************************************/

    private void handleB1(String address, byte[] data) {
        if (data[4] == (byte) 0xaa) {// 成功
            byte[] b2 = new byte[5];
            b2[0] = (byte) 0xb2;
            System.arraycopy(pairIds, 0, b2, 1, 4);
            bleService.write(address, b2);// 连线
        }
    }

    /************************************************连线成功*************************************************/

    private void handleB2(final String address) {
        Log.d(TAG, "b2连线成功" + address);
        if (pairedDevices.containsKey(address)) {// 自动重连情况
            Device device = pairedDevices.get(address);
            if (device != null) {
                device.isReady = false;
                device.connectionState = ConnectionType.CONNECTED;
                bleService.write(address, new byte[]{(byte) 0xa0});// 发A0拿历史数据
                device.historyTimer = new RepeatTimer(new RepeatTimer.Callback() {
                    @Override
                    public void onTimeOut() {
                        bleService.write(address, new byte[]{(byte) 0xa0});
                    }
                });
                device.historyTimer.startCount();
            }
        } else {// 新添加情况
            initNewDevice(address);
        }
    }

    private void initNewDevice(final String address) {
        Observable.just(1)
                .map(new Function<Integer, Device>() {
                    @Override
                    public Device apply(Integer integer) throws Exception {
                        Device device = new Device(address);
                        device.pairId = StringUtil.bytes2HexStringEx(pairIds);
                        device.pairedTime = Calendar.getInstance().getTimeInMillis();
                        device.connectionState = ConnectionType.CONNECTED;
                        device.isReady = true;
                        deviceDao.insert(device);
                        bleService.write(address, new byte[]{(byte) 0xf0, device.calH, device.calL});
                        bleService.write(address, new byte[]{(byte) 0xa7, device.chgOverH, device.chgOverL, device.chgH, device.chgL,
                                device.idleLowH, device.idleLowL, device.triggerH, device.triggerL, device.crankLowH, device.crankLowL});
                        pairedDevices.put(address, device);
                        return device;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Device>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Device device) {
                        handleConnectionConnected(device.address);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /************************************************连线失败*************************************************/

    private void handleEE(final String address, final byte[] data) {
        Log.d(TAG, "b2连接失败" + address);
        if (data[4] == (byte) 0x01) {// pair id 错误
            Device device = pairedDevices.get(address);
            if (device != null) {
                device.pairId = "";
                saveDeviceToDatabase(device);
            }
            pairedDevices.remove(address);
        }
    }

    /************************************************收到C0*************************************************/

    private void handleC0(final String address, final byte[] data) {
        final Device device = pairedDevices.get(address);
        if (device == null || !device.isReady) {
            Log.d(TAG, "未初始化完成");
            return;
        }
        final long now = Calendar.getInstance().getTimeInMillis();
        final long from = now - 60000;
        Observable.create(new ObservableOnSubscribe<Voltage>() {
            @Override
            public void subscribe(ObservableEmitter<Voltage> emitter) throws Exception {
                List<Voltage> voltages = voltageDao.getVoltagesByIndex(address, data[6], data[7], data[12], from, now);
                voltageDao.delete(voltages);

                Voltage voltage = new Voltage(address);
                voltage.time = now;
                float value = ValueUtil.getRealVoltage(data[4], data[5], data[9], data[10]);
                value = MathUtil.formatDouble2(value);
                voltage.value = value;
                voltage.indexH = data[6];
                voltage.indexL = data[7];
                voltage.reportId = data[12];
                float abnormalIdle = ValueUtil.getRealVoltage(device.idleLowH, device.idleLowL, device.calH, device.calL);
                float engineStop = ValueUtil.getRealVoltage(device.chgH, device.chgL, device.calH, device.calL);
                float overCharging = ValueUtil.getRealVoltage(device.chgOverH, device.chgOverL, device.calH, device.calL);
                if (voltage.value <= abnormalIdle) {
                    voltage.state = StateType.VOLTAGE_DYING;
                } else if (abnormalIdle < voltage.value && voltage.value < device.yellow) {
                    voltage.state = StateType.VOLTAGE_LOW;
                } else if (device.yellow < voltage.value && voltage.value < engineStop) {
                    voltage.state = StateType.VOLTAGE_GOOD;
                } else if (engineStop <= voltage.value && voltage.value < overCharging) {
                    voltage.state = StateType.CHARGING;
                } else {
                    voltage.state = StateType.OVER_CHARGING;
                }
                voltageDao.insert(voltage);
                emitter.onNext(voltage);
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Voltage>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Voltage voltage) {
                        handleC0State(address, data[11]);
                        Device device = pairedDevices.get(address);
                        if (device != null) {
                            device.voltage = voltage;
                        }
                        handleVoltageChangeListen(voltage);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /************************************************处理C0状态*************************************************/

    private void handleC0State(String address, byte state) {
        if ((state & 0x01) == 1) {
            // cranking
            final Device device = pairedDevices.get(address);
            if (device != null) {
                device.crankingDataSet = new CrankingDataSet(Calendar.getInstance().getTimeInMillis(), device.calH, device.calL);
                bleService.write(device.address, device.crankingDataSet.createA2Cmd());
                device.crankingTimer = new RepeatTimer(new RepeatTimer.Callback() {
                    @Override
                    public void onTimeOut() {
                        bleService.write(device.address, device.crankingDataSet.getA2());
                    }
                });
                device.crankingTimer.startCount();
            }
        }
        if (((state >> 1) & 0x01) == 1) {
            // over charging
        }
        if (((state >> 2) & 0x01) == 1) {
            // abnormal idle
        }
        if (((state >> 3) & 0x01) == 1) {
            // abnormal cranking
        }
    }

    /************************************************处理A0*************************************************/

    private void handleA0(final String address, final byte[] data) {
        final Device device = pairedDevices.get(address);
        if (device == null) {
            return;
        }
        if (device.historyTimer != null) {
            device.historyTimer.stopCount();
            device.historyTimer = null;
        }
        long startTime = 0;
        if (device.voltage != null) {
            startTime = device.voltage.time;
        }
        long now = Calendar.getInstance().getTimeInMillis();
        long max = 32767L * 1000 * 60;
        if (now - startTime > max) {
            startTime = now - max;
        }
        float abnormalIdle = ValueUtil.getRealVoltage(device.idleLowH, device.idleLowL, device.calH, device.calL);
        float engineStop = ValueUtil.getRealVoltage(device.chgH, device.chgL, device.calH, device.calL);
        float overCharging = ValueUtil.getRealVoltage(device.chgOverH, device.chgOverL, device.calH, device.calL);
        device.historyDataSet = new HistoryDataSet(device.address, abnormalIdle, device.yellow, engineStop, overCharging, device.calH, device.calL, data[6], data[7], startTime, now);
        device.isReady = true;
        bleService.write(device.address, device.historyDataSet.createA1Cmd());
        device.historyTimer = new RepeatTimer(new RepeatTimer.Callback() {
            @Override
            public void onTimeOut() {
                bleService.write(device.address, device.historyDataSet.getA1());
            }
        });
        device.historyTimer.startCount();
    }

    /************************************************获取历史*************************************************/

    private void handleA1(final String address, final byte[] data) {
        final Device device = pairedDevices.get(address);
        if (device == null) {
            return;
        }
        if (device.historyTimer != null) {
            device.historyTimer.stopCount();
            device.historyTimer = null;
        }
        if (device.historyDataSet == null) {
            return;
        }
        Observable.just(1)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        for (int i = 6; i < 126; i += 2) {
                            if (!device.historyDataSet.isEnd()) {
                                device.historyDataSet.putVoltage(data[i], data[i + 1]);
                            }
                            device.historyDataSet.next();
                        }
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .map(new Function<Integer, List<Voltage>>() {
                    @Override
                    public List<Voltage> apply(Integer integer) throws Exception {
                        if (device.historyDataSet.isEnd()) {
                            List<Voltage> voltages = device.historyDataSet.getReceivedData();
                            device.historyDataSet = null;
                            voltageDao.insert(voltages);
                            return voltages;
                        }
                        bleService.write(address, device.historyDataSet.createA1Cmd());
                        device.historyTimer = new RepeatTimer(new RepeatTimer.Callback() {
                            @Override
                            public void onTimeOut() {
                                bleService.write(device.address, device.historyDataSet.getA1());
                            }
                        });
                        device.historyTimer.startCount();
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Voltage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<Voltage> voltages) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /************************************************获取cranking*************************************************/

    private void handleA2(final String address, final byte[] data) {
        final Device device = pairedDevices.get(address);
        if (device == null) {
            return;
        }
        if (device.crankingTimer != null) {
            device.crankingTimer.stopCount();
            device.crankingTimer = null;
        }
        if (device.crankingDataSet == null) {
            return;
        }
        Observable.just(1)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        for (int i = 6; i < 134; i += 2) {
                            device.crankingDataSet.putValue(data[i], data[i + 1]);
                        }
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .map(new Function<Integer, Cranking>() {
                    @Override
                    public Cranking apply(Integer integer) throws Exception {
                        if (device.crankingDataSet.isEnd()) {
                            Cranking cranking = new Cranking(device.address);
                            cranking.startTime = device.crankingDataSet.getStartTime();
                            List<Float> values = device.crankingDataSet.getReceivedData();
                            cranking.value = new ArrayList<>();
                            for (int i = 0; i < values.size(); i++) {
                                float f = values.get(i);
                                f = MathUtil.formatDouble2(f);
                                CrankingValue value = new CrankingValue(device.address);
                                value.startTime = cranking.startTime;
                                value.value = f;
                                cranking.value.add(value);
                                if (f < cranking.minValue) {
                                    cranking.minValue = f;
                                }
                            }
                            float abnormalCranking = ValueUtil.getRealVoltage(device.crankLowH, device.crankLowL, device.calH, device.calL);
                            float crankingStart = ValueUtil.getRealVoltage(device.triggerH, device.triggerL, device.calH, device.calL);
                            float middle = (crankingStart + abnormalCranking) / 2;
                            if (cranking.minValue <= abnormalCranking) {
                                cranking.state = StateType.CRANKING_BAD;
                            } else if (abnormalCranking < cranking.minValue && cranking.minValue < middle) {
                                cranking.state = StateType.CRANKING_LOW;
                            } else {
                                cranking.state = StateType.CRANKING_GOOD;
                            }
                            crankingDao.insert(cranking);
                            crankingValueDao.insert(cranking.value);
                            device.crankingDataSet = null;
                            return cranking;
                        }
                        bleService.write(address, device.crankingDataSet.createA2Cmd());
                        device.crankingTimer = new RepeatTimer(new RepeatTimer.Callback() {
                            @Override
                            public void onTimeOut() {
                                bleService.write(device.address, device.crankingDataSet.getA2());
                            }
                        });
                        device.crankingTimer.startCount();
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Cranking>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Cranking cranking) {
                        device.cranking = cranking;
                        handleCrankingChangeListen(cranking);
                        bleService.write(device.address, new byte[]{(byte) 0xa3});
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /************************************************clear status*************************************************/

    private void handleA3(String address, byte[] data) {

    }

    /************************************************获取main setting*************************************************/

    private void handleA5(String address, byte[] data) {

    }

    /************************************************main setting*************************************************/

    private void handleA4(String address, byte[] data) {

    }

    // 以下需要service回调
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleEvent(final BleEvent event) {
        switch (event.type) {
            case BleEvent.BLUETOOTH_ADAPTER_DISABLE:// 蓝牙关闭
                for (Map.Entry<String, Device> entry : pairedDevices.entrySet()) {// 所有设备断开连接
                    entry.getValue().connectionState = ConnectionType.DISCONNECTED;
                    handleConnectionChangeListen(entry.getKey(), false);
                }
                break;
            case BleEvent.BLUETOOTH_ADAPTER_ENABLE:// 蓝牙启用
                break;
            case BleEvent.GATT_CONNECTED:// gatt已连接
                handleGattConnected(event.address);
                break;
            case BleEvent.GATT_DISCONNECTED:// gatt断开连接
                handleConnectionDisconnected(event.address);// 主动连接时
                handleGattDisconnected(event.address);
                handleConnectionChangeListen(event.address, false);
                break;
            case BleEvent.SEARCH_DEVICE_FOUND:// 发现设备
                handleAutoReconnect(event.address);
                handleSearchDeviceFound(event.address, event.name, event.rssi);
                break;
            case BleEvent.SEARCH_FINISH:// 搜索结束
                handleSearchFinish();
                break;
            case BleEvent.READ_DATA:
                if (event.value.length == 9 && event.value[3] == (byte) 0xb0) {// 请求配对反馈
                    handleB0(event.address, event.value);
                    return;
                }
                if (event.value.length == 6 && event.value[3] == (byte) 0xb1) {// 确认配对反馈
                    handleB1(event.address, event.value);
                    return;
                }
                if (event.value.length == 14 && event.value[3] == (byte) 0xb2) {// 连线成功反馈
                    handleConnectionChangeListen(event.address, true);
                    handleB2(event.address);
                    return;
                }
                if (event.value.length == 6 && event.value[3] == (byte) 0xee) {// 连线失败反馈
                    handleEE(event.address, event.value);
                    return;
                }
                if (event.value.length == 14 && event.value[3] == (byte) 0xa0) {// A0反馈
                    handleA0(event.address, event.value);
                    return;
                }
                if (event.value.length == 14 && event.value[3] == (byte) 0xc0) {// C0反馈
                    handleC0(event.address, event.value);
                    return;
                }
                if (event.value.length == 127 && event.value[3] == (byte) 0xa1) {// A1反馈
                    handleA1(event.address, event.value);
                    return;
                }
                if (event.value.length == 135 && event.value[3] == (byte) 0xa2) {// A2反馈(Cranking)
                    handleA2(event.address, event.value);
                    return;
                }
                if (event.value.length == 6 && event.value[3] == (byte) 0xa3) {// A3反馈 clear status
                    handleA3(event.address, event.value);
                    return;
                }
                if (event.value.length == 8 && event.value[3] == (byte) 0xa4) {// A4反馈 main settting
                    handleA4(event.address, event.value);
                    return;
                }
                if (event.value.length == 8 && event.value[3] == (byte) 0xa5) {// A5反馈 获取main setting
                    handleA5(event.address, event.value);
                    return;
                }
                if (event.value.length == 14 && event.value[3] == (byte) 0xa6) {// A6反馈 获取版本信息
                    handleA6(event.address, event.value);
                    return;
                }
                if (event.value.length == 15 && event.value[3] == (byte) 0xa7) {// A7反馈 voltage setting
                    handleA7(event.address, event.value);
                    return;
                }
                if (event.value.length == 14 && event.value[3] == (byte) 0xa8) {// A8反馈 usb setting
                    handleA8(event.address, event.value);
                    return;
                }
                if (event.value.length == 5 && event.value[3] == (byte) 0xa9) {// A9反馈 unpair
                    // 设备会自动断开gatt
                    return;
                }
                if (event.value.length == 5 && event.value[3] == (byte) 0xaa) {// AA反馈 recognize
                    handleAA(event.address);
                    return;
                }
                if (event.value.length == 6 && event.value[3] == (byte) 0xab) {// AB反馈 usb on off
                    handleAB(event.address, event.value);
                    return;
                }
                if (event.value.length == 7 && event.value[3] == (byte) 0xf0) {// F0反馈 calibration 电压校正
                    handleF0(event.address, event.value);
                    return;
                }
                break;
        }
    }
}
