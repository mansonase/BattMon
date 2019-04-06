package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.viseeointernational.battmon.R;

import java.util.ArrayList;
import java.util.List;

public class LongTimeChartView extends LineChart {

    private static final String TAG = ChartView.class.getSimpleName();

    public LongTimeChartView(Context context) {
        super(context);
        myInit();
    }

    public LongTimeChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        myInit();
    }

    public LongTimeChartView(Context context, AttributeSet attrs, int defStyle) {
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
        xAxis.setLabelCount(7, false);
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
        yAxis.setEnabled(false);

        YAxis rightAxis = getAxisRight();
        rightAxis.setEnabled(false);
        setDoubleTapToZoomEnabled(false);
        setPinchZoom(false);
        setVisibleXRange(30, 30);
    }

    private List<Entry> list = new ArrayList<>();

    public void setData(List<Entry> list, int position, float red, float yellow, float blue) {
        getAxisLeft().removeAllLimitLines();

        LimitLine limitLine = new LimitLine(red, String.valueOf(red));
        limitLine.setLineWidth(2);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        limitLine.setTextSize(12);
        limitLine.setLineColor(getResources().getColor(R.color.stateRed));
        getAxisLeft().addLimitLine(limitLine);

        limitLine = new LimitLine(yellow, String.valueOf(yellow));
        limitLine.setLineWidth(2);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        limitLine.setTextSize(12);
        limitLine.setLineColor(getResources().getColor(R.color.stateYellow));
        getAxisLeft().addLimitLine(limitLine);

        limitLine = new LimitLine(blue, String.valueOf(blue));
        limitLine.setLineWidth(2);
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        limitLine.setTextSize(12);
        limitLine.setLineColor(getResources().getColor(R.color.theme));
        getAxisLeft().addLimitLine(limitLine);

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
            lineDataSet.setColor(Color.WHITE);
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
