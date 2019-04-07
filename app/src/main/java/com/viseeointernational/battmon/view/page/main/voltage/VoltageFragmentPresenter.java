package com.viseeointernational.battmon.view.page.main.voltage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.github.mikephil.charting.data.Entry;
import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.constant.StateType;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.data.entity.Voltage;
import com.viseeointernational.battmon.data.source.device.DeviceSource;
import com.viseeointernational.battmon.util.MathUtil;
import com.viseeointernational.battmon.util.TimeUtil;
import com.viseeointernational.battmon.util.ValueUtil;
import com.viseeointernational.battmon.view.page.connect.ConnectActivity;
import com.viseeointernational.battmon.view.page.help.HelpActivity;
import com.viseeointernational.battmon.view.page.main.LongTimeChartType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class VoltageFragmentPresenter implements VoltageFragmentContract.Presenter {

    private static final String TAG = VoltageFragmentPresenter.class.getSimpleName();

    private VoltageFragmentContract.View view;

    private Context context;
    private DeviceSource deviceSource;

    private boolean shouldShowConnect;
    private boolean shouldClose;

    private float abnormalIdle, yellow, start;

    private int type;

    private int year;
    private long chartFrom;
    private long chartTo;
    private List<Voltage> tempChartData = new ArrayList<>();

    @Inject
    @Nullable
    String address;

    @Inject
    public VoltageFragmentPresenter(Context context, DeviceSource deviceSource) {
        this.context = context;
        this.deviceSource = deviceSource;
    }

    @Override
    public void takeView(VoltageFragmentContract.View view) {
        this.view = view;
        init();
    }

    @Override
    public void dropView() {
        deviceSource.setVoltageListener(null);
        view = null;
    }

    private void init() {
        deviceSource.setVoltageListener(new DeviceSource.VoltageListener() {
            @Override
            public void onValueReceived(Voltage voltage) {
                if (voltage.address.equals(address)) {
                    if (chartFrom <= voltage.time && voltage.time < chartTo) {
                        tempChartData.add(voltage);
                        makeVoltageChart(tempChartData, chartFrom, chartTo, -1);
                    }
                    showVoltage(voltage);
                }
            }
        });
        if (!deviceSource.isBleEnable()) {
            if (view != null) {
                view.showEnableBluetooth();
            }
            return;
        }
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        showYear();

        Observable.just(1)
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        deviceSource.init();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (shouldClose) {
                            shouldClose = false;
                            if (view != null) {
                                view.close();
                            }
                            return;
                        }
                        if (shouldShowConnect) {
                            shouldShowConnect = false;
                            if (view != null) {
                                view.showConnect();
                            }
                            return;
                        }
                        if (deviceSource.getDevicesCount() == 0) {
                            if (view != null) {
                                view.showHelp();
                            }
                            return;
                        }
                        if (TextUtils.isEmpty(address)) {
                            address = deviceSource.getDevices(null).get(0).address;
                            if (view != null) {
                                view.start(address);
                            }
                        }
                        Device device = deviceSource.getDevice(address);
                        abnormalIdle = ValueUtil.getRealVoltage(device.idleLowH, device.idleLowL, device.calH, device.calL);
                        start = abnormalIdle - 1;
                        yellow = device.yellow;
                        float overCharging = ValueUtil.getRealVoltage(device.chgOverH, device.chgOverL, device.calH, device.calL);
                        float end = overCharging + 1;
                        if (view != null) {
                            view.setThresholdvalue(start, abnormalIdle, yellow, overCharging, end);
                        }
                        if (device.voltage != null) {
                            showVoltage(device.voltage);
                        }
                        getChart();
                        getLongTimeChart(type);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void showVoltage(Voltage voltage) {
        if (view != null) {
            view.showValue(voltage.value);
            switch (voltage.state) {
                case StateType.VOLTAGE_DYING:
                    view.showState("Battery is Dying");
                    view.showColor(context.getResources().getColor(R.color.stateRed));
                    view.showFlash(false);
                    break;
                case StateType.VOLTAGE_LOW:
                    view.showState("Battery is Low");
                    view.showColor(context.getResources().getColor(R.color.stateYellow));
                    view.showFlash(false);
                    break;
                case StateType.VOLTAGE_GOOD:
                    view.showState("Battery is Good");
                    view.showColor(context.getResources().getColor(R.color.theme));
                    view.showFlash(false);
                    break;
                case StateType.CHARGING:
                    view.showState("Charging");
                    view.showColor(context.getResources().getColor(R.color.theme));
                    view.showFlash(true);
                    break;
                case StateType.OVER_CHARGING:
                    view.showState("Over Charging");
                    view.showColor(context.getResources().getColor(R.color.stateRed));
                    view.showFlash(true);
                    break;
            }
        }
    }

    private void getChart() {
        Calendar calendar = Calendar.getInstance();
        final long now = calendar.getTimeInMillis();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        chartFrom = calendar.getTimeInMillis();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, hour + 1);
        chartTo = calendar.getTimeInMillis();
        Observable.just(1)
                .map(new Function<Integer, List<Voltage>>() {
                    @Override
                    public List<Voltage> apply(Integer integer) throws Exception {
                        return deviceSource.getVoltages(address, chartFrom, chartTo);
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
                        tempChartData.clear();
                        tempChartData.addAll(voltages);
                        makeVoltageChart(voltages, chartFrom, chartTo, now);
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

    private void makeVoltageChart(final List<Voltage> voltages, final long from, long to, long now) {
        final int position;
        if (now == -1) {
            position = -1;
        } else {
            if (from <= now && now < to) {
                position = (int) ((now - from) / 10000);
            } else {
                position = -1;
            }
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
                            view.showChart(entries, position);
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
    public void result(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case VoltageFragment.REQUEST_HELP:
                switch (resultCode) {
                    case HelpActivity.RESULT_BACK:
                        shouldClose = true;
                        break;
                    case HelpActivity.RESULT_START:
                        shouldShowConnect = true;
                        break;
                }
                break;
            case VoltageFragment.REQUEST_CONNECT:
                if (resultCode == ConnectActivity.RESULT_BACK) {
                    shouldClose = true;
                }
                break;
        }
    }

    @Override
    public void showAbnormalCharging() {
        if (TextUtils.isEmpty(address)) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        final long now = calendar.getTimeInMillis();
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        final long from = calendar.getTimeInMillis();
        Observable.just(1)
                .map(new Function<Integer, List<Voltage>>() {
                    @Override
                    public List<Voltage> apply(Integer integer) throws Exception {
                        return deviceSource.getAbnormalChargings(address, from, now);
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
                        if (view != null) {
                            view.cancelLoading();
                            view.showAbnormalCharging(voltages);
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

    private void showYear() {
        if (view != null) {
            view.showYear(year + "");
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
}