package com.viseeointernational.battmon.view.page.main.cranking;

import com.github.mikephil.charting.data.Entry;
import com.viseeointernational.battmon.view.page.BasePresenter;
import com.viseeointernational.battmon.view.page.BaseView;

import java.util.List;

public interface CrankingFragmentContract {

    interface View extends BaseView {

        void showDate(String s);

        void showAnimation(float max, float min);

        void showValue(String s);

        void showState(String s);

        void showColor(int color);

        void setThresholdvalue(float start, float abnormalCranking, float yellow, float crankingStart);

        void showChart(List<Entry> data, int position);

        void showYear(String s);

        void showLongTimeChart(List<Entry> data, int position, float red, float yellow, float blue);

        void showPreviousItem(String s);

        void showNextItem(String s);
    }

    interface Presenter extends BasePresenter<View> {

        void previousYear();

        void nextYear();

        void setLongTimeChartType(int type);

        void previousItem();

        void nextItem();
    }
}
