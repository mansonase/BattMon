package com.viseeointernational.battmon.di.component;

import com.viseeointernational.battmon.di.ActivityScoped;
import com.viseeointernational.battmon.di.module.ConnectActivityModule;
import com.viseeointernational.battmon.view.page.connect.ConnectActivity;

import dagger.Subcomponent;

@ActivityScoped
@Subcomponent(modules = {ConnectActivityModule.class})
public interface ConnectActivityComponent {

    void inject(ConnectActivity activity);

    @Subcomponent.Builder
    interface Builder {
        ConnectActivityComponent build();
    }
}
