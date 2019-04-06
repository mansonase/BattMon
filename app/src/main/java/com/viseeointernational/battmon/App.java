package com.viseeointernational.battmon;

import android.app.Application;

import com.viseeointernational.battmon.data.source.android.ble.BleService;
import com.viseeointernational.battmon.di.component.AppComponent;
import com.viseeointernational.battmon.di.component.DaggerAppComponent;
import com.viseeointernational.battmon.di.module.AppModule;

public class App extends Application {

    private AppComponent appComponent;

    public void setBleService(BleService bleService) {
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(getApplicationContext(), bleService)).build();
    }

    /**
     * activity通过此方法获取主组件
     *
     * @return
     */
    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
