package com.viseeointernational.battmon.view.page.connect;

import android.content.Intent;

import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.view.page.BasePresenter;
import com.viseeointernational.battmon.view.page.BaseView;

import java.util.List;

public interface ConnectActivityContract {

    interface View extends BaseView {

        void showEnableBluetooth();

        void showDevices(List<Device> list);

        void alertIfReconnect();

        void close();
    }

    interface Presenter extends BasePresenter<View> {

        void result(int requestCode, int resultCode, Intent data);

        void search();

        void choose(Device device);

        void doConnection();
    }
}
