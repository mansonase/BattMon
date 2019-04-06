package com.viseeointernational.battmon.view.page;

/**
 * mvp框架vp相关 定义view通用方法
 */
public interface BaseView {

    /**
     * 显示toast
     *
     * @param id
     */
    void showMessage(int id);

    /**
     * 显示toast
     *
     * @param text
     */
    void showMessage(CharSequence text);

    /**
     * 显示加载中
     */
    void showLoading();

    /**
     * 显示加载中
     *
     * @param content 要展示的文案 默认是Waiting...
     */
    void showLoading(CharSequence content);

    /**
     * 停止加载中
     */
    void cancelLoading();
}
