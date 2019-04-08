package com.viseeointernational.battmon.view.page.main.cranking;

import android.content.Context;
import android.text.TextUtils;

import com.github.mikephil.charting.data.Entry;
import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.constant.StateType;
import com.viseeointernational.battmon.data.entity.Cranking;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.data.source.device.DeviceSource;
import com.viseeointernational.battmon.util.MathUtil;
import com.viseeointernational.battmon.util.TimeUtil;
import com.viseeointernational.battmon.util.ValueUtil;
import com.viseeointernational.battmon.view.page.main.LongTimeChartType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CrankingFragmentPresenter implements CrankingFragmentContract.Presenter {

    private static final String TAG = CrankingFragmentPresenter.class.getSimpleName();

    private CrankingFragmentContract.View view;

    private Context context;
    private DeviceSource deviceSource;

    private int year;

    private int type;
    private float start;
    private float abnormalCranking;
    private float yellow;

    @Inject
    @Nullable
    String address;

    @Inject
    public CrankingFragmentPresenter(Context context, DeviceSource deviceSource) {
        this.context = context;
        this.deviceSource = deviceSource;
    }

    @Override
    public void takeView(CrankingFragmentContract.View view) {
        this.view = view;
        init();
    }

    @Override
    public void dropView() {
        deviceSource.setCrankingListener(null);
        view = null;
    }

    private void init() {
        deviceSource.setCrankingListener(new DeviceSource.CrankingListener() {
            @Override
            public void onValueReceived(Cranking cranking) {
                if (cranking.address.equals(address)) {
                    showCranking(cranking);
                }
            }
        });
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        showYear();
        if (TextUtils.isEmpty(address)) {
            return;
        }
        Device device = deviceSource.getDevice(address);
        if (device.cranking != null) {
            showCranking(device.cranking);
            float crankingStart = ValueUtil.getRealVoltage(device.triggerH, device.triggerL, device.calH, device.calL);
            crankingStart = MathUtil.formatFloat1(crankingStart);
            abnormalCranking = ValueUtil.getRealVoltage(device.crankLowH, device.crankLowL, device.calH, device.calL);
            abnormalCranking = MathUtil.formatFloat1(abnormalCranking);
            yellow = abnormalCranking + 0.5f;
            start = abnormalCranking - 1;
            if (view != null) {
                view.setThresholdvalue(start, abnormalCranking, yellow, crankingStart);
            }
            getChart(device.cranking);
            getLongTimeChart(type);
        }
    }

    private void showCranking(Cranking cranking) {
        if (view != null) {
            view.showDate(TimeUtil.getFormatTime(cranking.startTime, "yyyy/MM/dd  HH:mm:ss"));
            view.showValue(cranking.minValue + "v");
            view.showAnimation(cranking.maxValue, cranking.minValue);
            switch (cranking.state) {
                case StateType.CRANKING_BAD:
                    view.showState("Bad");
                    view.showColor(context.getResources().getColor(R.color.stateRed));
                    break;
                case StateType.CRANKING_LOW:
                    view.showColor(context.getResources().getColor(R.color.stateYellow));
                    view.showState("Low");
                    break;
                case StateType.CRANKING_GOOD:
                    view.showColor(context.getResources().getColor(R.color.theme));
                    view.showState("Good");
                    break;
            }
        }
    }

    public void getChart(final Cranking cranking) {
        Observable.just(1)
                .map(new Function<Integer, List<Float>>() {
                    @Override
                    public List<Float> apply(Integer integer) throws Exception {
                        return cranking.getFloatValues();
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
                        makeCrankingChart(floats);
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

    private void makeCrankingChart(final List<Float> floats) {
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
                            view.showChart(entries, -1);
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
    public void previousYear() {
        year--;
        showYear();
        getLongTimeChart(type);
    }

    @Override
    public void nextYear() {
        year++;
        showYear();
        getLongTimeChart(type);
    }

    private void showYear() {
        if (view != null) {
            view.showYear(year + "");
        }
    }

    @Override
    public void setLongTimeChartType(int type) {
        this.type = type;
        getLongTimeChart(type);
    }

    private void getLongTimeChart(int type) {
        if (TextUtils.isEmpty(address)) {
            return;
        }
        final long interval = 10L * 24 * 60 * 60 * 1000;
        final Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        switch (type) {
            case LongTimeChartType.M_6:
            default:
                calendar.set(Calendar.MONTH, -6);
                break;
            case LongTimeChartType.Y_1:
                calendar.set(Calendar.MONTH, -18);
                break;
            case LongTimeChartType.Y_3:
                calendar.set(Calendar.MONTH, -30);
                break;
        }
        final long from = calendar.getTimeInMillis();
        final int size = (int) ((now - from) / interval) + 1;
        Observable.just(1)
                .map(new Function<Integer, List<Entry>>() {
                    @Override
                    public List<Entry> apply(Integer integer) throws Exception {
                        List<Entry> data = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            float value = deviceSource.getAvgCranking(address, from + i * interval, from + (i + 1) * interval);
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.setTimeInMillis(from + i * interval);
                            int month = calendar.get(Calendar.MONTH);
                            String xAxis = TimeUtil.getFormatTime(from + i * interval, TimeUtil.getEnglishMonthAbbr(month));
                            Entry entry = new Entry(i, value, xAxis);
                            data.add(entry);
                        }
                        return data;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Entry>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (view != null) {
                            view.showLoading();
                        }
                    }

                    @Override
                    public void onNext(List<Entry> entries) {
                        if (view != null) {
                            view.cancelLoading();
                            view.showLongTimeChart(entries, -1, start, yellow, abnormalCranking);
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
    public void previousItem() {

    }

    @Override
    public void nextItem() {

    }
}