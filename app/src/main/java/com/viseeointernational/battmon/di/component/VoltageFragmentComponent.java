package com.viseeointernational.battmon.di.component;

import android.support.v4.app.Fragment;

import com.viseeointernational.battmon.di.FragmentScoped;
import com.viseeointernational.battmon.di.module.VoltageFragmentModule;
import com.viseeointernational.battmon.view.page.main.voltage.VoltageFragment;

import dagger.BindsInstance;
import dagger.Subcomponent;

@FragmentScoped
@Subcomponent(modules = {VoltageFragmentModule.class})
public interface VoltageFragmentComponent {

    void inject(VoltageFragment fragment);

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        Builder fragment(Fragment fragment);

        VoltageFragmentComponent build();
    }
}
