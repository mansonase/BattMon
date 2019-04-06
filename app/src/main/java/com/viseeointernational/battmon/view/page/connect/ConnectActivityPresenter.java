package com.viseeointernational.battmon.view.page.connect;

import android.content.Intent;
import android.util.Log;

import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.data.source.device.DeviceSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;

public class ConnectActivityPresenter implements ConnectActivityContract.Presenter {

    private static final String TAG = ConnectActivityPresenter.class.getSimpleName();

    private ConnectActivityContract.View view;

    private DeviceSource deviceSource;

    private Map<String, Device> foundDevices = new LinkedHashMap<>();
    private String address;

    @Inject
    public ConnectActivityPresenter(DeviceSource deviceSource) {
        this.deviceSource = deviceSource;
    }

    @Override
    public void takeView(ConnectActivityContract.View view) {
        this.view = view;
        search();
    }

    @Override
    public void dropView() {
        view = null;
    }

    @Override
    public void search() {
        foundDevices.clear();
        if (view != null) {
            view.showDevices(new ArrayList<Device>(foundDevices.values()));
        }
        deviceSource.search(20, new DeviceSource.SearchCallback() {
            @Override
            public void onBleNotAvailable() {
                if (view != null) {
                    view.showEnableBluetooth();
                }
            }

            @Override
            public void onDeviceFound(Device device) {
                if (foundDevices.containsKey(device.address)) {
                    return;
                }
                foundDevices.put(device.address, device);
                if (view != null) {
                    view.showDevices(new ArrayList<>(foundDevices.values()));
                }
            }

            @Override
            public void onFinish() {
                if (view != null) {
                    if (foundDevices.size() == 0) {
//                        notifications.sendNoDeviceFoundNotification();
                    }
                }
            }
        });
    }

    @Override
    public void choose(Device device) {
        address = device.address;
        doConnection();
    }

    @Override
    public void doConnection() {
        if(view != null){
            view.showLoading();
        }
        deviceSource.connect(address, new DeviceSource.ConnectionCallback() {
            @Override
            public void onBleNotAvailable() {
                if (view != null) {
                    view.cancelLoading();
                    view.showEnableBluetooth();
                }
            }

            @Override
            public void onConnected() {
                Log.d(TAG, "onConnected ");
                if (view != null) {
                    view.cancelLoading();
                    view.showMessage(R.string.msg_successful);
                    view.close();
                }
            }

            @Override
            public void onDisconnected() {
                if (view != null) {
                    view.cancelLoading();
                    view.alertIfReconnect();
                }
            }
        });
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {

    }
}
