package com.viseeointernational.battmon.data.source.device;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.viseeointernational.battmon.data.entity.Cranking;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.data.entity.Trip;
import com.viseeointernational.battmon.data.entity.Voltage;

import java.util.List;

public interface DeviceSource {

    boolean isBleEnable();

    void onAppExit();

    void init();

    /**********************************************添加获取设备***************************************************/

    Device getDevice(@NonNull String address);// 主线程

    List<Device> getDevices(String search);// 主

    int getDevicesCount();// 主

    /**********************************************获取信息***************************************************/

    List<Voltage> getVoltages(@NonNull String address, long from, long to);// 子线程

    List<Voltage> getAbnormalChargings(@NonNull String address, long from, long to);// 子线程

    float getAvgVoltage(@NonNull String address, long from, long to);// 子线程

    float getAvgCranking(@NonNull String address, long from, long to);// 子线程

    List<Trip> getTripsAndDetail(@NonNull String address, long from, long to);// zi

    List<Trip> getTrips(@NonNull String address, long from, long to);// zi

    /**********************************************监听功能***************************************************/

    interface VoltageListener {

        void onValueReceived(Voltage voltage);
    }

    void setVoltageListener(@Nullable VoltageListener listener);

    interface CrankingListener {

        void onValueReceived(Cranking cranking);
    }

    void setCrankingListener(@Nullable CrankingListener listener);

    interface ConnectionChangeListener {

        void onConnectionChange(@NonNull String address, boolean isConnected);
    }

    void setConnectionChangeListener(@Nullable ConnectionChangeListener listener);

    interface TripListener{

        void onNewTrip(Trip trip);
    }

    void setTripListener(@Nullable TripListener listener);

    /**********************************************搜索***************************************************/

    interface SearchCallback {

        void onBleNotAvailable();

        void onDeviceFound(Device device);

        void onFinish();
    }

    void search(int seconds, @NonNull SearchCallback callback);

    void stopSearch();

    /**********************************************连接设备***************************************************/

    interface ConnectionCallback {

        void onBleNotAvailable();

        void onConnected();

        void onDisconnected();
    }

    void connect(@NonNull String address, @NonNull ConnectionCallback callback);

    /**********************************************设置***************************************************/

    void saveName(@NonNull String address, @NonNull String name);// zhu

    void saveHeader(@NonNull String address, String imagePath);// zhu

    void enableAbnormalNotification(@NonNull String address, boolean enable);// zhu

    interface GetVersionCallback {

        void onSuccessful(byte version, String name);

        void onFailed();

        void onTimeOut();

        void onBleNotAvailable();

        void onDeviceDisconnected();

        void onDeviceNotAvailable();
    }

    void getVersion(@NonNull String address, @NonNull GetVersionCallback callback);

    interface BleCallback {

        void onSuccessful();

        void onFailed();

        void onTimeOut();

        void onBleNotAvailable();

        void onDeviceDisconnected();

        void onDeviceNotAvailable();
    }

    void enableUsbPowerOff(@NonNull String address, boolean enable, @NonNull BleCallback callback);

    void delayUsbPowerOff(@NonNull String address, long startTime, long delayTime, @NonNull BleCallback callback);

    void recognize(@NonNull String address, @NonNull BleCallback callback);

    void unpair(@NonNull String address, boolean force, @NonNull BleCallback callback);

    void setCalibration(@NonNull String address, byte calH, byte calL);

    void setThresholdValue(@NonNull String address,
                           byte chgOverH, byte chgOverL,
                           byte chgH, byte chgL,
                           byte idleLowH, byte idleLowL,
                           byte triggerH, byte triggerL,
                           byte crankLowH, byte crankLowL);
}
