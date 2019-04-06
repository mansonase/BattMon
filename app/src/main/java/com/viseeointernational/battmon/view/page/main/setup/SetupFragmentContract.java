package com.viseeointernational.battmon.view.page.main.setup;

import com.viseeointernational.battmon.view.page.BasePresenter;
import com.viseeointernational.battmon.view.page.BaseView;

public interface SetupFragmentContract {

    interface View extends BaseView {

        void showAbnormalNotification(boolean enable);

        void showUsbPowerOff(boolean enable);

        void showUsbPowerOffAfter(String s);

        void showVersion(String s);

        void showQA(String web);

        void showGetNew(String web);

        void showCalibration(float value);

        void showAbnormalIdle(float value);

        void showOverCharging(float value);

        void showEngineStop(float value);

        void showCrankingStart(float value);

        void showAbnormalCranking(float value);
    }

    interface Presenter extends BasePresenter<View> {

        void enableAbnormalNotification(boolean enable);

        void enableUsbPowerOff(boolean enable);

        void setUsbPowerOffAfter(long time);

        void showQA();

        void showGetNew();

        void setCalibration(float value);

        void setAbnormalIdle(float value);

        void setOverCharging(float value);

        void setEngineStop(float value);

        void setCrankingStart(float value);

        void setAbnormalCranking(float value);
    }
}
