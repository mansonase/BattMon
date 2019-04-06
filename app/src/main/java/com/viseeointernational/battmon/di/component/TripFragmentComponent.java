package com.viseeointernational.battmon.di.component;

import android.support.v4.app.Fragment;

import com.viseeointernational.battmon.di.FragmentScoped;
import com.viseeointernational.battmon.di.module.TripFragmentModule;
import com.viseeointernational.battmon.view.page.main.trip.TripFragment;

import dagger.BindsInstance;
import dagger.Subcomponent;

@FragmentScoped
@Subcomponent(modules = {TripFragmentModule.class})
public interface TripFragmentComponent {

    void inject(TripFragment fragment);

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        Builder fragment(Fragment fragment);

        TripFragmentComponent build();
    }
}
