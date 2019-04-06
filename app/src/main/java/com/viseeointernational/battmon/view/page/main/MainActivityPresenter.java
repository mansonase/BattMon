package com.viseeointernational.battmon.view.page.main;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.viseeointernational.battmon.data.constant.ConnectionType;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.data.source.device.DeviceSource;

import javax.inject.Inject;

public class MainActivityPresenter implements MainActivityContract.Presenter {

    private MainActivityContract.View view;

    private DeviceSource deviceSource;

    private String address;

    @Inject
    public MainActivityPresenter(DeviceSource deviceSource) {
        this.deviceSource = deviceSource;
    }

    @Override
    public void takeView(MainActivityContract.View view) {
        this.view = view;
        init();
    }

    @Override
    public void dropView() {
        deviceSource.setConnectionChangeListener(null);
        view = null;
    }

    private void init() {
        deviceSource.setConnectionChangeListener(new DeviceSource.ConnectionChangeListener() {
            @Override
            public void onConnectionChange(@NonNull String address, boolean isConnected) {
                if (address.equals(MainActivityPresenter.this.address)) {
                    if (view != null) {
                        view.showConnection(isConnected);
                    }
                }
            }
        });
    }

    @Override
    public void changeDeviceInfo(String address) {
        if(address.equals(this.address)){
            Device device = deviceSource.getDevice(address);
            if (view != null) {
                view.showDeviceName(device.name);
                view.showDeviceHeader(device.imagePath);
                view.showConnection(device.connectionState == ConnectionType.CONNECTED);
            }
        }
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
        if (!TextUtils.isEmpty(address)) {
            Device device = deviceSource.getDevice(address);
            if (view != null) {
                view.showDeviceName(device.name);
                view.showDeviceHeader(device.imagePath);
                view.showConnection(device.connectionState == ConnectionType.CONNECTED);
            }
        } else {
            if (view != null) {
                view.showDeviceName("");
                view.showDeviceHeader(null);
                view.showConnection(false);
            }
        }
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public void onAppExit() {
        deviceSource.onAppExit();
    }
}
