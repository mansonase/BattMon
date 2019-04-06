package com.viseeointernational.battmon.view.page.main.voltage;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.data.entity.Voltage;
import com.viseeointernational.battmon.view.custom.AbnormalChargingDialog;
import com.viseeointernational.battmon.view.custom.BatteryView;
import com.viseeointernational.battmon.view.custom.ChartView;
import com.viseeointernational.battmon.view.custom.FlashView;
import com.viseeointernational.battmon.view.custom.LongTimeChartView;
import com.viseeointernational.battmon.view.custom.VoltageDialog;
import com.viseeointernational.battmon.view.page.BaseFragment;
import com.viseeointernational.battmon.view.page.connect.ConnectActivity;
import com.viseeointernational.battmon.view.page.help.HelpActivity;
import com.viseeointernational.battmon.view.page.main.LongTimeChartType;
import com.viseeointernational.battmon.view.page.main.MainActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class VoltageFragment extends BaseFragment implements VoltageFragmentContract.View {

    public static final int REQUEST_BLUETOOTH = 1;
    public static final int REQUEST_HELP = 2;
    public static final int REQUEST_CONNECT = 3;

    @Inject
    VoltageFragmentContract.Presenter presenter;

    @BindView(R.id.changer)
    ImageView changer;
    @BindView(R.id.flash)
    FlashView flash;
    @BindView(R.id.state)
    TextView state;
    @BindView(R.id.battery)
    BatteryView battery;
    @BindView(R.id.voltage)
    TextView voltage;
    @BindView(R.id.chart)
    ChartView chart;
    @BindView(R.id.year)
    TextView year;
    @BindView(R.id.m_6)
    RadioButton m6;
    @BindView(R.id.y_1)
    RadioButton y1;
    @BindView(R.id.y_3)
    RadioButton y3;
    @BindView(R.id.long_time_chart)
    LongTimeChartView longTimeChart;
    Unbinder unbinder;

    private boolean showBattery = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.getMainActivityComponent().voltageFragmentComponent().fragment(this).build().inject(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_voltage, container, false);
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
        m6.setOnCheckedChangeListener(onCheckedChangeListener);
        y1.setOnCheckedChangeListener(onCheckedChangeListener);
        y3.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                switch (buttonView.getId()) {
                    case R.id.m_6:
                        presenter.setLongTimeChartType(LongTimeChartType.M_6);
                        break;
                    case R.id.y_1:
                        presenter.setLongTimeChartType(LongTimeChartType.Y_1);
                        break;
                    case R.id.y_3:
                        presenter.setLongTimeChartType(LongTimeChartType.Y_3);
                        break;
                }
            }
        }
    };

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

    @Override
    public void showEnableBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_BLUETOOTH);
    }

    @Override
    public void showHelp() {
        Intent intent = new Intent(getActivity(), HelpActivity.class);
        intent.putExtra(HelpActivity.KEY_TYPE, HelpActivity.TYPE_FIRST_START);
        startActivityForResult(intent, REQUEST_HELP);
    }

    @Override
    public void showConnect() {
        Intent intent = new Intent(getActivity(), ConnectActivity.class);
        startActivityForResult(intent, REQUEST_CONNECT);
    }

    @Override
    public void start(String address) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.setAddress(address);
        }
    }

    @Override
    public void close() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    @Override
    public void showValue(float f) {
        battery.setVoltage(f);
        voltage.setText(String.valueOf(f) + "v");
    }

    @Override
    public void showState(String s) {
        state.setText(s);
    }

    @Override
    public void showColor(int color) {
        state.setTextColor(color);
        flash.setColor(color);
    }

    @Override
    public void showFlash(boolean enable) {
        flash.enableBreathe(enable);
    }

    @Override
    public void showChart(List<Entry> data, int position) {
        chart.setData(data, position);
    }

    @Override
    public void showAbnormalCharging(List<Voltage> list) {
        new AbnormalChargingDialog(getActivity()).show(list);
    }

    @Override
    public void showYear(String s) {
        year.setText(s);
    }

    @Override
    public void setThresholdvalue(float start, float abnormalIdle, float yellow, float overCharging, float end) {
        battery.setLevels(start, abnormalIdle, yellow, overCharging, end);
    }

    @Override
    public void showLongTimeChart(List<Entry> data, int position, float red, float yellow, float blue) {
        longTimeChart.setData(data, position, red, yellow, blue);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.result(requestCode, resultCode, data);
    }

    @OnClick({R.id.teach, R.id.changer, R.id.abnormal_charging, R.id.previous_year, R.id.next_year})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.teach:
                new VoltageDialog(getActivity()).show();
                break;
            case R.id.changer:
                if (showBattery) {
                    changer.setImageResource(R.mipmap.ic_voltage);
                    battery.setVisibility(View.GONE);
                    voltage.setVisibility(View.GONE);
                    chart.setVisibility(View.VISIBLE);
                } else {
                    changer.setImageResource(R.mipmap.ic_graph);
                    battery.setVisibility(View.VISIBLE);
                    voltage.setVisibility(View.VISIBLE);
                    chart.setVisibility(View.GONE);
                }
                showBattery = !showBattery;
                break;
            case R.id.abnormal_charging:
                presenter.showAbnormalCharging();
                break;
            case R.id.previous_year:
                presenter.previousYear();
                break;
            case R.id.next_year:
                presenter.nextYear();
                break;
        }
    }
}
