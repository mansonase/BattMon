package com.viseeointernational.battmon.di.component;

import com.viseeointernational.battmon.di.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    MainActivityComponent.Builder mainActivityComponent();

    ConnectActivityComponent.Builder connectActivityComponent();
}
