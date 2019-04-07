package com.viseeointernational.battmon.view.page.main.trip;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.github.mikephil.charting.data.Entry;
import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.entity.Cranking;
import com.viseeointernational.battmon.data.entity.Trip;
import com.viseeointernational.battmon.data.entity.Voltage;
import com.viseeointernational.battmon.data.source.device.DeviceSource;
import com.viseeointernational.battmon.util.MathUtil;
import com.viseeointernational.battmon.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
        if (year == 0) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        showDate(year, month, day);
        getTrip(year, month, day);
    }

    private void getTrip(final int year, final int month, final int day) {
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

    private void showDate(int year, int month, int day) {
        String date = TimeUtil.getNormalDate(year, month, day);
        if (view != null) {
            view.showDate(date);
        }
    }

    @Override
    public void showCalendar() {
        if (TextUtils.isEmpty(address)) {
            if (view != null) {
                view.showCalendar(year, month, null);
            }
            return;
        }
        getCalendarMarkers(year, month);
    }

    @Override
    public void selectDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        showDate(year, month, day);
        getTrip(year, month, day);
    }

    @Override
    public void changeMonth(int year, int month) {
        if (TextUtils.isEmpty(address)) {
            if (view != null) {
                view.showCalendar(year, month, null);
            }
            return;
        }
        getCalendarMarkers(year, month);
    }

    private void getCalendarMarkers(final int year, final int month) {
        if (TextUtils.isEmpty(address)) {
            return;
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        final long from = calendar.getTimeInMillis();
        calendar.set(Calendar.MONTH, month + 1);
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
                        for (int i = 0; i < trips.size(); i++) {
                            Trip trip = trips.get(i);
                            if (from <= trip.startTime) {
                                calendar.setTimeInMillis(trip.startTime);
                                ret.add(calendar.get(Calendar.DAY_OF_MONTH));
                            } else if (trip.endTime < to) {
                                calendar.setTimeInMillis(trip.endTime);
                                ret.add(calendar.get(Calendar.DAY_OF_MONTH));
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
    public void showVoltageGraph(final Trip trip) {
        if (TextUtils.isEmpty(address)) {
            return;
        }
        Observable.just(1)
                .map(new Function<Integer, List<Voltage>>() {
                    @Override
                    public List<Voltage> apply(Integer integer) throws Exception {
                        return deviceSource.getVoltages(address, trip.startTime, trip.endTime);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Voltage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (view != null) {
                            view.showLoading();
                        }
                    }

                    @Override
                    public void onNext(List<Voltage> voltages) {
                        makeVoltageChart(voltages, trip.startTime, trip.endTime);
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

    private void makeVoltageChart(final List<Voltage> voltages, final long from, long to) {
        final String title = TimeUtil.getNormalDateEx(from);
        long now = Calendar.getInstance().getTimeInMillis();
        final int position;
        if (from <= now && now < to) {
            position = (int) ((now - from) / 10000);
        } else {
            position = -1;
        }

        final int size = (int) ((to - from) / 10000) + 1;
        Observable.just(1)
                .map(new Function<Integer, List<Entry>>() {
                    @Override
                    public List<Entry> apply(Integer integer) throws Exception {
                        List<Entry> ret = new ArrayList<>();
                        Collections.reverse(voltages);
                        for (int i = 0; i < size; i++) {
                            long entryFrom = from + i * 10000L;
                            long entryTo = from + (i + 1) * 10000L;
                            List<Float> floats = new ArrayList<>();
                            for (int j = voltages.size() - 1; j >= 0; j--) {
                                Voltage voltage = voltages.get(j);
                                if (entryFrom <= voltage.time && voltage.time < entryTo) {
                                    floats.add(voltage.value);
                                    voltages.remove(j);
                                }
                            }
                            Entry entry;
                            if (floats.size() == 0) {
                                if (ret.size() == 0) {
                                    entry = new Entry(i, 0, TimeUtil.getFormatTime(entryFrom, "HH:mm"));
                                } else {
                                    Entry lastEntry = ret.get(ret.size() - 1);
                                    entry = new Entry(i, lastEntry.getY(), TimeUtil.getFormatTime(entryFrom, "HH:mm"));
                                }
                            } else {
                                float value = 0;
                                for (int j = 0; j < floats.size(); j++) {
                                    value += floats.get(j);
                                }
                                value = value / floats.size();
                                value = MathUtil.formatFloat2(value);
                                entry = new Entry(i, value, TimeUtil.getFormatTime(entryFrom, "HH:mm"));
                            }
                            ret.add(entry);
                        }
                        return ret;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Entry>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<Entry> entries) {
                        if (view != null) {
                            view.cancelLoading();
                            view.showVoltageGraph(R.drawable.ic_voltage, title, entries, position);
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
    public void showCrankingGraph(final Trip trip) {
        Observable.just(1)
                .map(new Function<Integer, List<Float>>() {
                    @Override
                    public List<Float> apply(Integer integer) throws Exception {
                        if (trip.cranking != null) {
                            return trip.cranking.getFloatValues();
                        }
                        return null;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Float>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (view != null) {
                            view.showLoading();
                        }
                    }

                    @Override
                    public void onNext(List<Float> floats) {
                        makeCrankingChart(floats, trip.cranking.startTime);
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

    private void makeCrankingChart(final List<Float> floats, long from) {
        final String title = TimeUtil.getNormalDateEx(from);
        final int position = -1;
        Observable.just(1)
                .map(new Function<Integer, List<Entry>>() {
                    @Override
                    public List<Entry> apply(Integer integer) throws Exception {
                        List<Entry> ret = new ArrayList<>();
                        List<Float> temp = new ArrayList<>();
                        int index = 0;
                        for (int i = 0; i < floats.size(); i++) {
                            if (i % 5 == 0) {
                                if (temp.size() != 0) {
                                    float value = 0;
                                    for (int j = 0; j < temp.size(); j++) {
                                        value += temp.get(j);
                                    }
                                    value = value / temp.size();
                                    value = MathUtil.formatFloat2(value);
                                    float axis = (i / 5f - 1) * 0.1f;
                                    axis = MathUtil.formatFloat2(axis);
                                    Entry entry = new Entry(index, value, axis + "s");
                                    ret.add(entry);
                                    index++;
                                }
                                temp.clear();
                            } else {
                                temp.add(floats.get(i));
                            }
                        }

                        return ret;
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Entry>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<Entry> entries) {
                        if (view != null) {
                            view.cancelLoading();
                            view.showCrankingGraph(R.drawable.ic_cranking, title, entries, position);
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
}