package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.viseeointernational.battmon.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VoltageView extends RelativeLayout {

    private static final String TAG = VoltageView.class.getSimpleName();

    @BindView(R.id.pointer)
    ImageView pointer;

    public VoltageView(Context context) {
        super(context);
        init(context);
    }

    public VoltageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VoltageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public VoltageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.voltage, this);
        ButterKnife.bind(this);
    }

    // 各种颜色占的角度
    private static final float BEFORE_RED = 10;
    private static final float RED = 52;
    private static final float YELLOW = 64;
    private static final float BLUE = 120;
    private static final float AFTER_BLUE = 10;

    // 边界值
    private float level0 = 7.5f;
    private float level1 = 8.5f;
    private float level2 = 9.5f;
    private float level3 = 10.5f;

    // 各种颜色每单位值占的角度
    private float blueStep = BLUE / (level3 - level2);
    private float yellowStep = YELLOW / (level2 - level1);
    private float redStep = RED / (level1 - level0);

    private float lastDegrees;

    public void setLevels(float level0, float level1, float level2, float level3) {
        this.level0 = level0;
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;

        blueStep = BLUE / (level3 - level2);
        yellowStep = YELLOW / (level2 - level1);
        redStep = RED / (level1 - level0);
    }

    public void setVoltage(float voltage) {
        float degrees;
        if (level3 < voltage) {
            degrees = BEFORE_RED + RED + YELLOW + BLUE + AFTER_BLUE;
        } else if (level2 < voltage && voltage <= level3) {
            degrees = BEFORE_RED + RED + YELLOW + blueStep * (voltage - level2);
        } else if (level1 < voltage && voltage <= level2) {
            degrees = BEFORE_RED + RED + yellowStep * (voltage - level1);
        } else if (level0 <= voltage && voltage <= level1) {
            degrees = BEFORE_RED + redStep * (voltage - level0);
        } else {
            degrees = 0;
        }
//        degrees -= 143;
        doRotation(degrees);
        lastDegrees = degrees;
    }

    private void doRotation(float degrees) {
        RotateAnimation animation = new RotateAnimation(lastDegrees, degrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(100);
        animation.setFillAfter(true);
        pointer.startAnimation(animation);
    }
}
