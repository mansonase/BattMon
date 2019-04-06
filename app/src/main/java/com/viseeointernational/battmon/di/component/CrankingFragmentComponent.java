package com.viseeointernational.battmon.di.component;

import android.support.v4.app.Fragment;

import com.viseeointernational.battmon.di.FragmentScoped;
import com.viseeointernational.battmon.di.module.CrankingFragmentModule;
import com.viseeointernational.battmon.view.page.main.cranking.CrankingFragment;

import dagger.BindsInstance;
import dagger.Subcomponent;

@FragmentScoped
@Subcomponent(modules = {CrankingFragmentModule.class})
public interface CrankingFragmentComponent {

    void inject(CrankingFragment fragment);

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        Builder fragment(Fragment fragment);

        CrankingFragmentComponent build();
    }
}
