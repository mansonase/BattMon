package com.viseeointernational.battmon.di.module;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.viseeointernational.battmon.di.FragmentScoped;
import com.viseeointernational.battmon.view.page.main.MainActivity;
import com.viseeointernational.battmon.view.page.main.voltage.VoltageFragmentContract;
import com.viseeointernational.battmon.view.page.main.voltage.VoltageFragmentPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class VoltageFragmentModule {

    @FragmentScoped
    @Provides
    VoltageFragmentContract.Presenter presenter(VoltageFragmentPresenter presenter) {
        return presenter;
    }

    @FragmentScoped
    @Nullable
    @Provides
    String address(Fragment fragment) {
        if (fragment.getArguments() != null) {
            return fragment.getArguments().getString(MainActivity.KEY_ADDRESS, null);
        }
        return null;
    }
}
