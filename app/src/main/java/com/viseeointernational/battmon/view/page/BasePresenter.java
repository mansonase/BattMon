package com.viseeointernational.battmon.view.page;

public interface BasePresenter<T> {

    void takeView(T view);

    void dropView();
}
