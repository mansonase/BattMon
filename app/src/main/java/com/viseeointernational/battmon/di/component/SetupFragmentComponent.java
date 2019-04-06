package com.viseeointernational.battmon.di.component;

import android.support.v4.app.Fragment;

import com.viseeointernational.battmon.di.FragmentScoped;
import com.viseeointernational.battmon.di.module.SetupFragmentModule;
import com.viseeointernational.battmon.view.page.main.setup.SetupFragment;

import dagger.BindsInstance;
import dagger.Subcomponent;

@FragmentScoped
@Subcomponent(modules = {SetupFragmentModule.class})
public interface SetupFragmentComponent {

    void inject(SetupFragment fragment);

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        Builder fragment(Fragment fragment);

        SetupFragmentComponent build();
    }
}
