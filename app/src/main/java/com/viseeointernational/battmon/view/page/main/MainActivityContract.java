package com.viseeointernational.battmon.view.page.main;

import com.viseeointernational.battmon.view.page.BasePresenter;
import com.viseeointernational.battmon.view.page.BaseView;

public interface MainActivityContract {

    interface View extends BaseView {

        void showDeviceName(String name);

        void showDeviceHeader(String s);

        void showConnection(boolean isConnected);
    }

    interface Presenter extends BasePresenter<View> {

        void changeDeviceInfo(String address);

        void setAddress(String address);

        String getAddress();

        void onAppExit();
    }
}
