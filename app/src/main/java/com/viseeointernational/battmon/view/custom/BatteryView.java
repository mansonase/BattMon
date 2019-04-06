package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viseeointernational.battmon.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BatteryView extends LinearLayout {

    @BindView(R.id.wave)
    WaveView wave;
    @BindView(R.id.text_1)
    TextView text1;
    @BindView(R.id.text_2)
    TextView text2;
    @BindView(R.id.text_3)
    TextView text3;

    public BatteryView(Context context) {
        super(context);
        init(context);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.battery, this);
        ButterKnife.bind(this);
    }

    private static final float RED_1 = 20;
    private static final float YELLOW = 20;
    private static final float BLUE = 40;
    private static final float RED_2 = 20;

    // 边界值
    private float level0 = 7.5f;
    private float level1 = 8.5f;
    private float level2 = 9.5f;
    private float level3 = 10.5f;
    private float level4 = 10.5f;

    // 各种颜色每单位值占的角度
    private float red1Step = RED_1 / (level1 - level0);
    private float yellowStep = YELLOW / (level2 - level1);
    private float blueStep = BLUE / (level3 - level2);
    private float red2Step = RED_2 / (level4 - level3);

    public void setLevels(float level0, float level1, float level2, float level3, float level4) {
        this.level0 = level0;
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
        this.level4 = level4;

        red1Step = RED_1 / (level1 - level0);
        yellowStep = YELLOW / (level2 - level1);
        blueStep = BLUE / (level3 - level2);
        red2Step = RED_2 / (level4 - level3);
    }

    public void setVoltage(float voltage) {
        float percent;
        if (level4 < voltage) {
            percent = RED_1 + YELLOW + BLUE + RED_2;
            wave.setColor(getResources().getColor(R.color.stateRed));
        } else if (level3 < voltage && voltage <= level4) {
            percent = RED_1 + YELLOW + BLUE + red2Step * (voltage - level3);
            wave.setColor(getResources().getColor(R.color.stateRed));
        } else if (level2 < voltage && voltage <= level3) {
            percent = RED_1 + YELLOW + blueStep * (voltage - level2);
            wave.setColor(getResources().getColor(R.color.theme));
        } else if (level1 <= voltage && voltage <= level2) {
            percent = RED_1 + yellowStep * (voltage - level1);
            wave.setColor(getResources().getColor(R.color.stateYellow));
        } else if (level0 <= voltage && voltage <= level1) {
            percent = red1Step * (voltage - level0);
            wave.setColor(getResources().getColor(R.color.stateRed));
        } else {
            percent = 0;
            wave.setColor(getResources().getColor(R.color.stateRed));
        }
        wave.setProgress(percent);
    }
}
