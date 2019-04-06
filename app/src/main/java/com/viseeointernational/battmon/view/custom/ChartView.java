package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.viseeointernational.battmon.R;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends LineChart {

    private static final String TAG = ChartView.class.getSimpleName();

    public ChartView(Context context) {
        super(context);
        myInit();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        myInit();
    }

    public ChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        myInit();
    }

    protected void myInit() {
        getDescription().setEnabled(false);
        getLegend().setEnabled(false);
        setNoDataText("No data");
        setNoDataTextColor(Color.WHITE);

        XAxis xAxis = getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(5, false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int position = (int) value;
                if (list.size() > position) {
                    Entry entry = list.get(position);
                    Object object = entry.getData();
                    if (object instanceof String) {
                        return (String) object;
                    }
                }
                return "";
            }
        });

        YAxis yAxis = getAxisLeft();
        yAxis.setAxisLineColor(Color.TRANSPARENT);
        yAxis.setTextColor(Color.WHITE);
        yAxis.setTextSize(12);
        yAxis.setAxisLineWidth(2);
        yAxis.setDrawGridLines(true);
        yAxis.setLabelCount(6, false);
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value) + "v";
            }
        });

        YAxis rightAxis = getAxisRight();
        rightAxis.setEnabled(false);
        setDoubleTapToZoomEnabled(false);
        setPinchZoom(false);
        setVisibleXRange(30, 30);
    }

    private List<Entry> list = new ArrayList<>();

    public void setData(List<Entry> list, int position) {
        this.list.clear();
        this.list.addAll(list);
        clear();
        LineDataSet lineDataSet;
        if (getData() != null && getData().getDataSetCount() != 0) {
            lineDataSet = (LineDataSet) getData().getDataSetByIndex(0);
            lineDataSet.setValues(list);
            getData().notifyDataChanged();
            notifyDataSetChanged();
        } else {
            lineDataSet = new LineDataSet(list, "");
            lineDataSet.setColor(getResources().getColor(R.color.theme));
            lineDataSet.setDrawCircleHole(false);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setLineWidth(4);
            LineData lineData = new LineData(lineDataSet);
            lineData.setDrawValues(false);
            setData(lineData);
        }
        if (position != -1) {
            zoom(1, 1, position, 1, YAxis.AxisDependency.LEFT);
        }
    }

}
