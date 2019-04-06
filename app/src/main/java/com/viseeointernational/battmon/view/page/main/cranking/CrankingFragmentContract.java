package com.viseeointernational.battmon.view.page.main.cranking;

import com.viseeointernational.battmon.view.page.BasePresenter;
import com.viseeointernational.battmon.view.page.BaseView;

import java.util.List;

public interface CrankingFragmentContract {

    interface View extends BaseView {

        void showDate(String s);

        void showAnimation(List<Float> data);

        void showValue(String s);

        void showState(String s);

        void showColor(int color);

        void setThresholdvalue(float start, float abnormalCranking, float yellow, float crankingStart);
    }

    interface Presenter extends BasePresenter<View> {

    }
}
