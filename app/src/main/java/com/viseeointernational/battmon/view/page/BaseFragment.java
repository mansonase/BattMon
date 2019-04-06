package com.viseeointernational.battmon.view.page;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment implements BaseView {

    @Override
    public void showMessage(int id) {
        BaseActivity activity = getBaseActivity();
        if (activity != null) {
            activity.showMessage(id);
        }
    }

    @Override
    public void showMessage(CharSequence text) {
        BaseActivity activity = getBaseActivity();
        if (activity != null) {
            activity.showMessage(text);
        }
    }

    @Override
    public void showLoading() {
        BaseActivity activity = getBaseActivity();
        if (activity != null) {
            activity.showLoading();
        }
    }

    @Override
    public void showLoading(CharSequence content) {
        BaseActivity activity = getBaseActivity();
        if (activity != null) {
            activity.showLoading(content);
        }
    }

    @Override
    public void cancelLoading() {
        BaseActivity activity = getBaseActivity();
        if (activity != null) {
            activity.cancelLoading();
        }
    }

    private BaseActivity getBaseActivity() {
        Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            return (BaseActivity) activity;
        }
        return null;
    }

}
