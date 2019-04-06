package com.viseeointernational.battmon.view.page.main.setup;

import android.support.annotation.Nullable;

import com.viseeointernational.battmon.BuildConfig;
import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.data.source.device.DeviceSource;
import com.viseeointernational.battmon.util.ValueUtil;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SetupFragmentPresenter implements SetupFragmentContract.Presenter {

    private static final String TAG = SetupFragmentPresenter.class.getSimpleName();

    private SetupFragmentContract.View view;

    private DeviceSource deviceSource;

    @Inject
    @Nullable
    String address;

    @Inject
    public SetupFragmentPresenter(DeviceSource deviceSource) {
        this.deviceSource = deviceSource;
    }

    @Override
    public void takeView(SetupFragmentContract.View view) {
        this.view = view;
        init();
        startDelayTimer();
        startVoltageTimer();
    }

    @Override
    public void dropView() {
        saveVoltage();
        stopVoltageTimer();
        stopDelayTimer();
        view = null;
    }

    private byte calH;// 电压校正高位
    private byte calL = 1;// 电压校正地位

    private byte chgOverH;// 过充报警电压
    private byte chgOverL;

    private byte chgH;// 充电电压
    private byte chgL;

    private byte idleLowH;// 平常最低报警电压
    private byte idleLowL;

    private byte triggerH;// 启动触发电压
    private byte triggerL;

    private byte crankLowH;// 启动最低报警电压
    private byte crankLowL;

    private void init() {
        if (view != null) {
            view.showVersion("V" + BuildConfig.VERSION_NAME);
        }
        if (address != null) {
            Device device = deviceSource.getDevice(address);
            if (device == null) {
                return;
            }
            if (view != null) {
                this.calH = device.calH;
                this.calL = device.calL;
                int calibration = ValueUtil.getValue(device.calH, device.calL);
                view.showCalibration(calibration);

                this.idleLowH = device.idleLowH;
                this.idleLowL = device.idleLowL;
                float abnormalIdle = ValueUtil.getRealVoltage(device.idleLowH, device.idleLowL, device.calH, device.calL);
                view.showAbnormalIdle(abnormalIdle);

                this.chgOverH = device.chgOverH;
                this.chgOverL = device.chgOverL;
                float overCharging = ValueUtil.getRealVoltage(device.chgOverH, device.chgOverL, device.calH, device.calL);
                view.showOverCharging(overCharging);

                this.chgH = device.chgH;
                this.chgL = device.chgL;
                float engineStop = ValueUtil.getRealVoltage(device.chgH, device.chgL, device.calH, device.calL);
                view.showEngineStop(engineStop);

                this.triggerH = device.triggerH;
                this.triggerL = device.triggerL;
                float crankingStart = ValueUtil.getRealVoltage(device.triggerH, device.triggerL, device.calH, device.calL);
                view.showCrankingStart(crankingStart);

                this.crankLowH = device.crankLowH;
                this.crankLowL = device.crankLowL;
                float abnormalCranking = ValueUtil.getRealVoltage(device.crankLowH, device.crankLowL, device.calH, device.calL);
                view.showAbnormalCranking(abnormalCranking);

                view.showAbnormalNotification(device.enableAbnormalNotification);
                view.showUsbPowerOff(device.enableUsbPowerOff);
            }
        }
    }

    private Disposable disposable;

    private void startDelayTimer() {
        stopDelayTimer();
        disposable = Observable.interval(0, 1, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (address == null) {
                            return;
                        }
                        Device device = deviceSource.getDevice(address);
                        if (device == null) {
                            return;
                        }
                        if (device.usbPowerOffDelayTime == -1) {
                            view.showUsbPowerOffAfter("Always On");
                        } else {
                            long now = Calendar.getInstance().getTimeInMillis();
                            long interval = device.usbPowerOffDelayTime - (now - device.usbPowerOffStartTime);
                            if (interval > 0) {
                                int hours = (int) (interval / (60000L * 60));
                                int minutes = (int) (interval - hours * 60000L * 60) / 60000;
                                String s = "";
                                if (hours == 1) {
                                    s = hours + "hour";
                                } else if (hours > 0) {
                                    s = hours + "hours";
                                }
                                if (minutes == 1) {
                                    s += minutes + "min";
                                } else if (minutes > 0) {
                                    s += minutes + "mins";
                                }
                                view.showUsbPowerOffAfter(s);
                            } else {
                                view.showUsbPowerOffAfter("--");
                            }
                        }
                    }
                });
    }

    private void stopDelayTimer() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    private Disposable voltageDisposable;

    private void startVoltageTimer() {
        stopVoltageTimer();
        voltageDisposable = Observable.interval(10, 10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        saveVoltage();
                    }
                });
    }

    private void stopVoltageTimer() {
        if (voltageDisposable != null && !voltageDisposable.isDisposed()) {
            voltageDisposable.dispose();
            voltageDisposable = null;
        }
    }

    private void saveVoltage() {
        if (address != null) {
            deviceSource.setCalibration(address, calH, calL);
            deviceSource.setThresholdValue(address, chgOverH, chgOverL, chgH, chgL,
                    idleLowH, idleLowL,
                    triggerH, triggerL,
                    crankLowH, crankLowL);
        }
    }

    @Override
    public void enableAbnormalNotification(boolean enable) {
        if (address != null) {
            deviceSource.enableAbnormalNotification(address, enable);
        }
    }

    private boolean tempEnableUsbPowerOff;

    @Override
    public void enableUsbPowerOff(boolean enable) {
        if (address != null) {
            tempEnableUsbPowerOff = !enable;
            if (view != null) {
                view.showLoading();
            }
            deviceSource.enableUsbPowerOff(address, enable, new DeviceSource.BleCallback() {
                @Override
                public void onFailed() {
                    if (view != null) {
                        view.cancelLoading();
                        view.showMessage(R.string.msg_failed);
                        view.showUsbPowerOff(tempEnableUsbPowerOff);
                    }
                }

                @Override
                public void onSuccessful() {
                    if (view != null) {
                        view.cancelLoading();
                        view.showMessage(R.string.msg_successful);
                    }
                }

                @Override
                public void onTimeOut() {
                    if (view != null) {
                        view.cancelLoading();
                        view.showMessage(R.string.msg_time_out);
                        view.showUsbPowerOff(tempEnableUsbPowerOff);
                    }
                }

                @Override
                public void onBleNotAvailable() {
                    if (view != null) {
                        view.cancelLoading();
                        view.showMessage(R.string.msg_ble_not_enable);
                        view.showUsbPowerOff(tempEnableUsbPowerOff);
                    }
                }

                @Override
                public void onDeviceDisconnected() {
                    if (view != null) {
                        view.cancelLoading();
                        view.showMessage(R.string.msg_device_offline);
                        view.showUsbPowerOff(tempEnableUsbPowerOff);
                    }
                }

                @Override
                public void onDeviceNotAvailable() {
                    if (view != null) {
                        view.cancelLoading();
                        view.showMessage(R.string.msg_device_not_available);
                        view.showUsbPowerOff(tempEnableUsbPowerOff);
                    }
                }
            });
        }
    }

    @Override
    public void setUsbPowerOffAfter(long time) {
        if (address != null) {
            if (view != null) {
                view.showLoading();
            }
            deviceSource.delayUsbPowerOff(address, Calendar.getInstance().getTimeInMillis(), time, new DeviceSource.BleCallback() {
                @Override
                public void onFailed() {
                    if (view != null) {
                        view.cancelLoading();
                        view.showMessage(R.string.msg_failed);
                    }
                }

                @Override
                public void onSuccessful() {
                    if (view != null) {
                        view.cancelLoading();
                        view.showMessage(R.string.msg_successful);
                    }
                }

                @Override
                public void onTimeOut() {
                    if (view != null) {
                        view.cancelLoading();
                        view.showMessage(R.string.msg_time_out);
                    }
                }

                @Override
                public void onBleNotAvailable() {
                    if (view != null) {
                        view.cancelLoading();
                        view.showMessage(R.string.msg_ble_not_enable);
                    }
                }

                @Override
                public void onDeviceDisconnected() {
                    if (view != null) {
                        view.cancelLoading();
                        view.showMessage(R.string.msg_device_offline);
                    }
                }

                @Override
                public void onDeviceNotAvailable() {
                    if (view != null) {
                        view.cancelLoading();
                        view.showMessage(R.string.msg_device_not_available);
                    }
                }
            });
        }
    }

    @Override
    public void showQA() {
        if (view != null) {
            view.showQA("https://www.baidu.com");
        }
    }

    @Override
    public void showGetNew() {
        if (view != null) {
            view.showGetNew("https://www.baidu.com");
        }
    }

    @Override
    public void setCalibration(float value) {
        calH = ValueUtil.getH((int) value);
        calL = ValueUtil.getL((int) value);
    }

    @Override
    public void setAbnormalIdle(float value) {
        int voltage = ValueUtil.getVoltage(value, calH, calL);
        idleLowH = ValueUtil.getH(voltage);
        idleLowL = ValueUtil.getL(voltage);
    }

    @Override
    public void setOverCharging(float value) {
        int voltage = ValueUtil.getVoltage(value, calH, calL);
        chgOverH = ValueUtil.getH(voltage);
        chgOverL = ValueUtil.getL(voltage);
    }

    @Override
    public void setEngineStop(float value) {
        int voltage = ValueUtil.getVoltage(value, calH, calL);
        chgH = ValueUtil.getH(voltage);
        chgL = ValueUtil.getL(voltage);
    }

    @Override
    public void setCrankingStart(float value) {
        int voltage = ValueUtil.getVoltage(value, calH, calL);
        triggerH = ValueUtil.getH(voltage);
        triggerL = ValueUtil.getL(voltage);
    }

    @Override
    public void setAbnormalCranking(float value) {
        int voltage = ValueUtil.getVoltage(value, calH, calL);
        crankLowH = ValueUtil.getH(voltage);
        crankLowL = ValueUtil.getL(voltage);
    }
}