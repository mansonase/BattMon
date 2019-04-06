package com.viseeointernational.battmon.di.module;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.viseeointernational.battmon.di.FragmentScoped;
import com.viseeointernational.battmon.view.adapter.TripAdapter;
import com.viseeointernational.battmon.view.page.main.MainActivity;
import com.viseeointernational.battmon.view.page.main.trip.TripFragmentContract;
import com.viseeointernational.battmon.view.page.main.trip.TripFragmentPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class TripFragmentModule {

    @FragmentScoped
    @Provides
    TripFragmentContract.Presenter presenter(TripFragmentPresenter presenter) {
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

    @FragmentScoped
    @Provides
    TripAdapter adapter(Context context) {
        return new TripAdapter(context);
    }
}
