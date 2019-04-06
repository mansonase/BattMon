package com.viseeointernational.battmon.view.page.main.list;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.view.page.BasePresenter;
import com.viseeointernational.battmon.view.page.BaseView;

import java.util.List;

public interface ListFragmentContract {

    interface View extends BaseView {

        void showDevices(List<Device> devices);

        void cropHeader(Uri origin, Uri output);

        void takePhoto(Uri output);

        void showAdd();

        void clearEdit();

        void changDeviceInfo(String address);
    }

    interface Presenter extends BasePresenter<View> {

        void add();

        void editDevice(@NonNull Device device);

        void deleteHeader();

        void takePhoto();

        void result(int requestCode, int resultCode, Intent data);

        void setName(String s);

        void delete();

        void searchDevice(@NonNull String input);
    }
}
