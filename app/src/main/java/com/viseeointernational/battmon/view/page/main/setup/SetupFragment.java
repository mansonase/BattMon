package com.viseeointernational.battmon.view.page.main.setup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.view.custom.PickerDialog;
import com.viseeointernational.battmon.view.custom.ValueView;
import com.viseeointernational.battmon.view.page.BaseFragment;
import com.viseeointernational.battmon.view.page.main.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SetupFragment extends BaseFragment implements SetupFragmentContract.View {

    @Inject
    SetupFragmentContract.Presenter presenter;

    Unbinder unbinder;
    @BindView(R.id.advanced)
    CheckBox advanced;
    @BindView(R.id.calibration)
    ValueView calibration;
    @BindView(R.id.abnormal_idle_bar)
    TextView abnormalIdleBar;
    @BindView(R.id.over_charging_bar)
    TextView overChargingBar;
    @BindView(R.id.abnormal_idle)
    ValueView abnormalIdle;
    @BindView(R.id.over_charging)
    ValueView overCharging;
    @BindView(R.id.engine_stop)
    ValueView engineStop;
    @BindView(R.id.cranking_start_graph)
    TextView crankingStartGraph;
    @BindView(R.id.abnormal_cranking_graph)
    TextView abnormalCrankingGraph;
    @BindView(R.id.cranking_start)
    ValueView crankingStart;
    @BindView(R.id.abnormal_cranking)
    ValueView abnormalCranking;
    @BindView(R.id.advanced_content)
    LinearLayout advancedContent;
    @BindView(R.id.abnormal_notification)
    Switch abnormalNotification;
    @BindView(R.id.usb_power_off)
    Switch usbPowerOff;
    @BindView(R.id.power_off_after_value)
    TextView powerOffAfterValue;
    @BindView(R.id.version)
    TextView version;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.getMainActivityComponent().setupFragmentComponent().fragment(this).build().inject(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setup, container, false);
        unbinder = ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        abnormalNotification.setOnCheckedChangeListener(onCheckedChangeListener);
        usbPowerOff.setOnCheckedChangeListener(onCheckedChangeListener);
        advanced.setOnCheckedChangeListener(onCheckedChangeListener);

        calibration.setOnValueChangeListener(onValueChangeListener);
        abnormalIdle.setOnValueChangeListener(onValueChangeListener);
        overCharging.setOnValueChangeListener(onValueChangeListener);
        engineStop.setOnValueChangeListener(onValueChangeListener);
        crankingStart.setOnValueChangeListener(onValueChangeListener);
        abnormalCranking.setOnValueChangeListener(onValueChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.takeView(this);
    }

    @Override
    public void onPause() {
        presenter.dropView();
        super.onPause();
    }

    private ValueView.OnValueChangeListener onValueChangeListener = new ValueView.OnValueChangeListener() {
        @Override
        public void onValueChange(ValueView view, float value, String formatValue) {
            switch (view.getId()) {
                case R.id.calibration:
                    presenter.setCalibration(value);
                    break;
                case R.id.abnormal_idle:
                    abnormalIdleBar.setText(formatValue + "v");
                    presenter.setAbnormalIdle(value);
                    break;
                case R.id.over_charging:
                    overChargingBar.setText(formatValue + "v");
                    presenter.setOverCharging(value);
                    break;
                case R.id.engine_stop:
                    presenter.setEngineStop(value);
                    break;
                case R.id.cranking_start:
                    crankingStartGraph.setText(formatValue + "v");
                    presenter.setCrankingStart(value);
                    break;
                case R.id.abnormal_cranking:
                    abnormalCrankingGraph.setText(formatValue + "v");
                    presenter.setAbnormalCranking(value);
                    break;
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.abnormal_notification:
                    presenter.enableAbnormalNotification(isChecked);
                    break;
                case R.id.usb_power_off:
                    presenter.enableUsbPowerOff(isChecked);
                    break;
                case R.id.advanced:
                    if (isChecked) {
                        advancedContent.setVisibility(View.VISIBLE);
                    } else {
                        advancedContent.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    @Override
    public void showAbnormalNotification(boolean enable) {
        abnormalNotification.setOnCheckedChangeListener(null);
        abnormalNotification.setChecked(enable);
        abnormalNotification.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void showUsbPowerOff(boolean enable) {
        usbPowerOff.setOnCheckedChangeListener(null);
        usbPowerOff.setChecked(enable);
        usbPowerOff.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    @Override
    public void showUsbPowerOffAfter(String s) {
        powerOffAfterValue.setText(s);
    }

    @Override
    public void showVersion(String s) {
        version.setText(s);
    }

    @Override
    public void showQA(String web) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(web));
        startActivity(intent);
    }

    @Override
    public void showGetNew(String web) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(web));
        startActivity(intent);
    }

    @Override
    public void showCalibration(float value) {
        calibration.setOnValueChangeListener(null);
        calibration.setValue(value);
        calibration.setOnValueChangeListener(onValueChangeListener);
    }

    @Override
    public void showAbnormalIdle(float value) {
        abnormalIdle.setOnValueChangeListener(null);
        abnormalIdle.setValue(value);
        abnormalIdleBar.setText(abnormalIdle.getFormatedValue(value) + "v");
        abnormalIdle.setOnValueChangeListener(onValueChangeListener);
    }

    @Override
    public void showOverCharging(float value) {
        overCharging.setOnValueChangeListener(null);
        overCharging.setValue(value);
        overChargingBar.setText(overCharging.getFormatedValue(value) + "v");
        overCharging.setOnValueChangeListener(onValueChangeListener);
    }

    @Override
    public void showEngineStop(float value) {
        engineStop.setOnValueChangeListener(null);
        engineStop.setValue(value);
        engineStop.setOnValueChangeListener(onValueChangeListener);
    }

    @Override
    public void showCrankingStart(float value) {
        crankingStart.setOnValueChangeListener(null);
        crankingStart.setValue(value);
        crankingStartGraph.setText(crankingStart.getFormatedValue(value) + "v");
        crankingStart.setOnValueChangeListener(onValueChangeListener);
    }

    @Override
    public void showAbnormalCranking(float value) {
        abnormalCranking.setOnValueChangeListener(null);
        abnormalCranking.setValue(value);
        abnormalCrankingGraph.setText(abnormalCranking.getFormatedValue(value) + "v");
        abnormalCranking.setOnValueChangeListener(onValueChangeListener);
    }

    @OnClick({R.id.power_off_after, R.id.introduction, R.id.qa, R.id.get_new})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.power_off_after:
                new PickerDialog(getActivity(), new PickerDialog.Callback() {
                    @Override
                    public void onSelect(PickerDialog dialog, long time) {
                        presenter.setUsbPowerOffAfter(time);
                    }
                }).show();
                break;
            case R.id.introduction:
                // todo
                break;
            case R.id.qa:
                presenter.showQA();
                break;
            case R.id.get_new:
                presenter.showGetNew();
                break;
        }
    }
}
