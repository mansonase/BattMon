package com.viseeointernational.battmon.di.module;

import android.content.Context;

import com.viseeointernational.battmon.di.FragmentScoped;
import com.viseeointernational.battmon.view.adapter.ListAdapter;
import com.viseeointernational.battmon.view.page.main.list.ListFragmentContract;
import com.viseeointernational.battmon.view.page.main.list.ListFragmentPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class ListFragmentModule {

    @FragmentScoped
    @Provides
    ListFragmentContract.Presenter presenter(ListFragmentPresenter presenter) {
        return presenter;
    }

    @FragmentScoped
    @Provides
    ListAdapter adapter(Context context){
        return new ListAdapter(context);
    }
}
