package com.viseeointernational.battmon.di.component;

import com.viseeointernational.battmon.di.FragmentScoped;
import com.viseeointernational.battmon.di.module.ListFragmentModule;
import com.viseeointernational.battmon.view.page.main.list.ListFragment;

import dagger.Subcomponent;

/**
 * 继承MainActivityComponent
 */
@FragmentScoped
@Subcomponent(modules = {ListFragmentModule.class})
public interface ListFragmentComponent {

    void inject(ListFragment fragment);

    @Subcomponent.Builder
    interface Builder {

        ListFragmentComponent build();
    }
}
