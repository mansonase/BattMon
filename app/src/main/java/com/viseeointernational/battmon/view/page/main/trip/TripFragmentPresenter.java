package com.viseeointernational.battmon.view.page.main.trip;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.viseeointernational.battmon.data.entity.Trip;
import com.viseeointernational.battmon.data.source.device.DeviceSource;
import com.viseeointernational.battmon.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TripFragmentPresenter implements TripFragmentContract.Presenter {

    private static final String TAG = TripFragmentPresenter.class.getSimpleName();

    private TripFragmentContract.View view;

    private DeviceSource deviceSource;

    private int year;
    private int month;
    private int day;

    @Inject
    @Nullable
    String address;

    @Inject
    public TripFragmentPresenter(DeviceSource deviceSource) {
        this.deviceSource = deviceSource;
    }

    @Override
    public void takeView(TripFragmentContract.View view) {
        this.view = view;
        init();
    }

    @Override
    public void dropView() {
        view = null;
    }

    private void init() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        showDate();
        getTrip();
    }

    private void getTrip() {
        if (TextUtils.isEmpty(address)) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        final long from = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, day + 1);
        final long to = calendar.getTimeInMillis();
        Observable.just(1)
                .map(new Function<Integer, List<Trip>>() {
                    @Override
                    public List<Trip> apply(Integer integer) throws Exception {
                        return deviceSource.getTripsAndDetail(address, from, to);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Trip>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (view != null) {
                            view.showLoading();
                        }
                    }

                    @Override
                    public void onNext(List<Trip> trips) {
                        if (view != null) {
                            view.cancelLoading();
                            view.showTrips(trips);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (view != null) {
                            view.cancelLoading();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        long time = calendar.getTimeInMillis();
        if (view != null) {
            view.showDate(TimeUtil.getFormatTime(time, "yyyy/MM/dd"));
        }
    }

    @Override
    public void showCalendar() {
        if (view != null) {
            view.showCalendar(year, month, null);
        }
    }

    @Override
    public void selectDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        showDate();
        getTrip();
    }

    @Override
    public void changeMonth(final int year, final int month) {
        if (TextUtils.isEmpty(address)) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        final long from = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, month + 1);
        final long to = calendar.getTimeInMillis();
        Observable.just(1)
                .map(new Function<Integer, List<Trip>>() {
                    @Override
                    public List<Trip> apply(Integer integer) throws Exception {
                        return deviceSource.getTrips(address, from, to);
                    }
                })
                .map(new Function<List<Trip>, List<Integer>>() {
                    @Override
                    public List<Integer> apply(List<Trip> trips) throws Exception {
                        List<Integer> ret = new ArrayList<>();
                        Calendar calendar1 = Calendar.getInstance();
                        for (int i = 0; i < trips.size(); i++) {
                            Trip trip = trips.get(i);
                            int day = -1;
                            if (from <= trip.startTime) {
                                calendar1.setTimeInMillis(trip.startTime);
                                day = calendar1.get(Calendar.DAY_OF_MONTH);
                            } else if (trip.endTime < to) {
                                calendar1.setTimeInMillis(trip.endTime);
                                day = calendar1.get(Calendar.DAY_OF_MONTH);
                            }
                            if (day != -1 && !ret.contains(day)) {
                                ret.add(day);
                            }
                        }
                        return ret;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (view != null) {
                            view.showLoading();
                        }
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        if (view != null) {
                            view.cancelLoading();
                            view.showCalendar(year, month, integers);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (view != null) {
                            view.cancelLoading();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void showVoltageGraph(Trip trip) {

    }

    @Override
    public void showCrankingGraph(Trip trip) {

    }
}