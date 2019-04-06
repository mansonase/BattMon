package com.viseeointernational.battmon.di.component;

import com.viseeointernational.battmon.di.ActivityScoped;
import com.viseeointernational.battmon.di.module.MainActivityModule;
import com.viseeointernational.battmon.view.page.main.MainActivity;

import dagger.Subcomponent;

@ActivityScoped
@Subcomponent(modules = {MainActivityModule.class})
public interface MainActivityComponent {

    void inject(MainActivity activity);

    @Subcomponent.Builder
    interface Builder {
        MainActivityComponent build();
    }

    CrankingFragmentComponent.Builder crankingFragmentComponent();

    ListFragmentComponent.Builder chargingFragmentComponent();

    SetupFragmentComponent.Builder setupFragmentComponent();

    TripFragmentComponent.Builder tripFragmentComponent();

    VoltageFragmentComponent.Builder voltageFragmentComponent();
}
