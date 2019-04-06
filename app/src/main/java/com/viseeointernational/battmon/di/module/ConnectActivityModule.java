package com.viseeointernational.battmon.di.module;

import android.content.Context;

import com.viseeointernational.battmon.di.ActivityScoped;
import com.viseeointernational.battmon.view.adapter.DeviceAdapter;
import com.viseeointernational.battmon.view.page.connect.ConnectActivityContract;
import com.viseeointernational.battmon.view.page.connect.ConnectActivityPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ConnectActivityModule {

    @ActivityScoped
    @Provides
    ConnectActivityContract.Presenter presenter(ConnectActivityPresenter presenter) {
        return presenter;
    }

    @ActivityScoped
    @Provides
    DeviceAdapter adapter(Context context) {
        return new DeviceAdapter(context);
    }
}
