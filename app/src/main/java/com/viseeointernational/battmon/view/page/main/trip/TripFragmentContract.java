package com.viseeointernational.battmon.view.page.main.trip;

import com.github.mikephil.charting.data.Entry;
import com.viseeointernational.battmon.data.entity.Trip;
import com.viseeointernational.battmon.view.page.BasePresenter;
import com.viseeointernational.battmon.view.page.BaseView;

import java.util.List;

public interface TripFragmentContract {

    interface View extends BaseView {

        void showDate(String s);

        void showTrips(List<Trip> list);

        void showCalendar(int year, int month, List<Integer> markedDays);

        void showVoltageGraph(int icon, String title, List<Entry> list, int position);

        void showCrankingGraph(int icon, String title, List<Entry> list, int position);
    }

    interface Presenter extends BasePresenter<View> {

        void showCalendar();

        void selectDate(int year, int month, int day);

        void changeMonth(int year, int month);

        void showVoltageGraph(Trip trip);

        void showCrankingGraph(Trip trip);
    }
}
