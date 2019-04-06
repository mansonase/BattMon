package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.viseeointernational.battmon.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChartDialog extends AppCompatDialog {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.chart)
    ChartView chart;

    public ChartDialog(Context context) {
        super(context, R.style.DialogBase);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_chart);
        ButterKnife.bind(this);
    }

    public void show(String title, List<Entry> data, int position) {
        super.show();
        this.title.setText(title);
        chart.setData(data, position);
    }
}
