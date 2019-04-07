package com.viseeointernational.battmon.view.page.main.cranking;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.viseeointernational.battmon.R;
import com.viseeointernational.battmon.view.custom.ChartView;
import com.viseeointernational.battmon.view.custom.CrankingDialog;
import com.viseeointernational.battmon.view.custom.FlashView;
import com.viseeointernational.battmon.view.custom.LongTimeChartView;
import com.viseeointernational.battmon.view.custom.VoltageView;
import com.viseeointernational.battmon.view.page.BaseFragment;
import com.viseeointernational.battmon.view.page.main.MainActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CrankingFragment extends BaseFragment implements CrankingFragmentContract.View {

    @Inject
    CrankingFragmentContract.Presenter presenter;

    @BindView(R.id.changer)
    ImageView changer;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.voltage)
    VoltageView voltage;
    @BindView(R.id.cranking_start)
    TextView crankingStart;
    @BindView(R.id.abnormal_cranking)
    TextView abnormalCranking;
    @BindView(R.id.yellow_cranking)
    TextView yellowCranking;
    @BindView(R.id.cranking)
    TextView cranking;
    @BindView(R.id.flash)
    FlashView flash;
    @BindView(R.id.state)
    TextView state;
    @BindView(R.id.value_content)
    LinearLayout valueContent;
    @BindView(R.id.previous_date)
    TextView previousDate;
    @BindView(R.id.next_date)
    TextView nextDate;
    @BindView(R.id.chart_bar)
    LinearLayout chartBar;
    @BindView(R.id.chart)
    ChartView chart;
    @BindView(R.id.m_6)
    RadioButton m6;
    @BindView(R.id.y_1)
    RadioButton y1;
    @BindView(R.id.y_3)
    RadioButton y3;
    @BindView(R.id.year)
    TextView year;
    @BindView(R.id.long_time_chart)
    LongTimeChartView longTimeChart;
    Unbinder unbinder;

    private boolean showInstrument = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.getMainActivityComponent().crankingFragmentComponent().fragment(this).build().inject(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cranking, container, false);
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

    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.takeView(this);
    }

    @Override
    public void onPause() {
        stopAnimation();
        presenter.dropView();
        super.onPause();
    }

    @Override
    public void showDate(String s) {
        date.setText(s);
    }

    private Disposable disposable;

    @Override
    public void showAnimation(final List<Float> data) {
        stopAnimation();
        disposable = Observable.interval(0, 110, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {

                    int index;

                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (index < data.size()) {
                            voltage.setVoltage(data.get(index));
                            index++;
                        } else {
                            stopAnimation();
                        }
                    }
                });
    }

    private void stopAnimation() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            disposable = null;
        }
    }

    @Override
    public void showValue(String s) {
        cranking.setText(s);
    }

    @Override
    public void showState(String s) {
        state.setText(s);
    }

    @Override
    public void showColor(int color) {
        flash.setColor(color);
        state.setTextColor(color);
    }

    @Override
    public void setThresholdvalue(float start, float abnormalCranking, float yellow, float crankingStart) {
        voltage.setLevels(start, abnormalCranking, yellow, crankingStart);
    }

    @Override
    public void showChart(List<Entry> data, int position) {
        chart.setData(data, position);
    }

    @Override
    public void showYear(String s) {
        year.setText(s);
    }

    @Override
    public void showLongTimeChart(List<Entry> data, int position, float red, float yellow, float blue) {
        longTimeChart.setData(data, position, red, yellow, blue);
    }

    @Override
    public void showPreviousItem(String s) {
        previousDate.setText(s);
    }

    @Override
    public void showNextItem(String s) {
        nextDate.setText(s);
    }

    @OnClick({R.id.teach, R.id.changer, R.id.previous_item, R.id.previous_date, R.id.next_date, R.id.next_item, R.id.previous_year, R.id.next_year})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.teach:
                new CrankingDialog(getActivity()).show();
                break;
            case R.id.changer:
                if (showInstrument) {
                    changer.setImageResource(R.mipmap.ic_cranking);
                    valueContent.setVisibility(View.GONE);
                    chart.setVisibility(View.VISIBLE);
                    chartBar.setVisibility(View.VISIBLE);
                } else {
                    changer.setImageResource(R.mipmap.ic_graph);
                    valueContent.setVisibility(View.VISIBLE);
                    chart.setVisibility(View.GONE);
                    chartBar.setVisibility(View.GONE);
                }
                showInstrument = !showInstrument;
                break;
            case R.id.previous_item:
            case R.id.previous_date:

                break;
            case R.id.next_date:
            case R.id.next_item:

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
