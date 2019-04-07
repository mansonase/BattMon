package com.viseeointernational.battmon.view.page.main.cranking;

import android.content.Context;
import android.text.TextUtils;

import com.github.mikephil.charting.data.Entry;
import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.constant.StateType;
import com.viseeointernational.battmon.data.entity.Cranking;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.data.source.device.DeviceSource;
import com.viseeointernational.battmon.util.TimeUtil;
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
        if (TextUtils.isEmpty(address)) {
            return;
        }
        Device device = deviceSource.getDevice(address);
        if (device.cranking != null) {
            showCranking(device.cranking);
        }
    }

    private void showCranking(Cranking cranking) {
        if (view != null) {
            view.showDate(TimeUtil.getFormatTime(cranking.startTime, "yyyy/MM/dd  HH:mm:ss"));
            view.showValue(cranking.minValue + "v");
            view.showAnimation(cranking.getFloatValues());
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

    @Override
    public void previousYear() {
        year--;
        showYear();
    }

    @Override
    public void nextYear() {
        year++;
        showYear();
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
                            float value = deviceSource.getAvgVoltage(address, from + i * interval, from + (i + 1) * interval);
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
                            view.showLongTimeChart(entries, -1, start, abnormalIdle, yellow);
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