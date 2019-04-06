package com.viseeointernational.battmon.view.page.main.voltage;

import android.content.Intent;

import com.github.mikephil.charting.data.Entry;
import com.viseeointernational.battmon.data.entity.Voltage;
import com.viseeointernational.battmon.view.page.BasePresenter;
import com.viseeointernational.battmon.view.page.BaseView;

import java.util.List;

public interface VoltageFragmentContract {

    interface View extends BaseView {

        void showEnableBluetooth();

        void showHelp();

        void showConnect();

        void start(String address);

        void close();


        void showState(String s);

        void showColor(int color);

        void showFlash(boolean enable);

        void showValue(float f);

        void setThresholdvalue(float start, float abnormalIdle, float yellow, float overCharging, float end);

        void showChart(List<Entry> data, int position);

        void showAbnormalCharging(List<Voltage> list);

        void showYear(String s);

        void showLongTimeChart(List<Entry> data, int position, float red, float yellow, float blue);
    }

    interface Presenter extends BasePresenter<View> {

        void result(int requestCode, int resultCode, Intent data);

        void showAbnormalCharging();

        void previousYear();

        void nextYear();

        void setLongTimeChartType(int type);
    }
}
