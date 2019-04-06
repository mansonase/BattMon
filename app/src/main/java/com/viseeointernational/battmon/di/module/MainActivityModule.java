package com.viseeointernational.battmon.di.module;

import com.viseeointernational.battmon.di.ActivityScoped;
import com.viseeointernational.battmon.di.component.CrankingFragmentComponent;
import com.viseeointernational.battmon.di.component.ListFragmentComponent;
import com.viseeointernational.battmon.di.component.SetupFragmentComponent;
import com.viseeointernational.battmon.di.component.TripFragmentComponent;
import com.viseeointernational.battmon.di.component.VoltageFragmentComponent;
import com.viseeointernational.battmon.view.page.main.MainActivityContract;
import com.viseeointernational.battmon.view.page.main.MainActivityPresenter;
import com.viseeointernational.battmon.view.page.main.cranking.CrankingFragment;
import com.viseeointernational.battmon.view.page.main.list.ListFragment;
import com.viseeointernational.battmon.view.page.main.setup.SetupFragment;
import com.viseeointernational.battmon.view.page.main.trip.TripFragment;
import com.viseeointernational.battmon.view.page.main.voltage.VoltageFragment;

import dagger.Module;
import dagger.Provides;

@Module(subcomponents = {CrankingFragmentComponent.class,
        ListFragmentComponent.class,
        SetupFragmentComponent.class,
        TripFragmentComponent.class,
        VoltageFragmentComponent.class})
public class MainActivityModule {

    @ActivityScoped
    @Provides
    ListFragment chargingFragment() {
        return new ListFragment();
    }

    @ActivityScoped
    @Provides
    CrankingFragment crankingFragment() {
        return new CrankingFragment();
    }

    @ActivityScoped
    @Provides
    SetupFragment setupFragment() {
        return new SetupFragment();
    }

    @ActivityScoped
    @Provides
    TripFragment tripFragment() {
        return new TripFragment();
    }

    @ActivityScoped
    @Provides
    VoltageFragment voltageFragment() {
        return new VoltageFragment();
    }

    @ActivityScoped
    @Provides
    MainActivityContract.Presenter presenter(MainActivityPresenter presenter) {
        return presenter;
    }
}
