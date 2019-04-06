package com.viseeointernational.battmon.view.page.main.cranking;

import android.content.Context;
import android.text.TextUtils;

import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.constant.StateType;
import com.viseeointernational.battmon.data.entity.Cranking;
import com.viseeointernational.battmon.data.entity.CrankingValue;
import com.viseeointernational.battmon.data.entity.Device;
import com.viseeointernational.battmon.data.source.device.DeviceSource;
import com.viseeointernational.battmon.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.annotations.Nullable;

public class CrankingFragmentPresenter implements CrankingFragmentContract.Presenter {

    private static final String TAG = CrankingFragmentPresenter.class.getSimpleName();

    private CrankingFragmentContract.View view;

    private Context context;
    private DeviceSource deviceSource;

    private int year;

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
            List<CrankingValue> values = cranking.value;
            List<Float> data = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                data.add(values.get(i).value);
            }
            view.showAnimation(data);
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
}