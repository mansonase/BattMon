package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viseeointernational.battmon.R;

import java.math.BigDecimal;
import java.math.RoundingMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ValueView extends LinearLayout {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.value)
    EditText value;

    public ValueView(Context context) {
        super(context);
    }

    public ValueView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ValueView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ValueView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.value, this);
        ButterKnife.bind(this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ValueView);
        text = typedArray.getString(R.styleable.ValueView_text);
        defaultValue = typedArray.getFloat(R.styleable.ValueView_defaultValue, 12f);
        step = typedArray.getFloat(R.styleable.ValueView_step, 0.1f);
        max = typedArray.getFloat(R.styleable.ValueView_max, 13f);
        min = typedArray.getFloat(R.styleable.ValueView_min, 11f);
        decimalLength = typedArray.getInt(R.styleable.ValueView_decimalLength, 1);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        value.setText(getFormatedValue(defaultValue));
        value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                try {
                    float f = Float.valueOf(input);
                    if (f > 0) {
                        String string = getFormatedValue(f);
                        if (onValueChangeListener != null) {
                            onValueChangeListener.onValueChange(ValueView.this, f, string);
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        name.setText(text);
    }

    private CharSequence text = "";
    private float defaultValue;
    private float step;
    private float max;
    private float min;
    private int decimalLength;

    private OnValueChangeListener onValueChangeListener;

    public float getValue() {
        float f = defaultValue;
        try {
            f = Float.valueOf(value.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return f;
    }

    public void setValue(float f) {
        value.setText(getFormatedValue(f));
    }

    public void setOnValueChangeListener(OnValueChangeListener onValueChangeListener) {
        this.onValueChangeListener = onValueChangeListener;
    }

    @OnClick({R.id.sub, R.id.add, R.id.default_value})
    public void onViewClicked(View view) {
        float f = defaultValue;
        try {
            f = Float.valueOf(value.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        String s;
        switch (view.getId()) {
            case R.id.sub:
                if (f > min) {
                    f -= step;
                    if (f < min) {
                        f = min;
                    }
                    s = getFormatedValue(f);
                    if (onValueChangeListener != null) {
                        onValueChangeListener.onValueChange(this, f, s);
                    }
                    value.setText(s);
                }
                break;
            case R.id.add:
                if (f < max) {
                    f += step;
                    if (f > max) {
                        f = max;
                    }
                    s = getFormatedValue(f);
                    if (onValueChangeListener != null) {
                        onValueChangeListener.onValueChange(this, f, s);
                    }
                    value.setText(s);
                }
                break;
            case R.id.default_value:
                s = getFormatedValue(defaultValue);
                if (onValueChangeListener != null) {
                    onValueChangeListener.onValueChange(this, defaultValue, s);
                }
                value.setText(s);
                break;
        }
    }

    public String getFormatedValue(float value) {
        return new BigDecimal(value).setScale(decimalLength, RoundingMode.HALF_UP).toString();
    }

    public interface OnValueChangeListener {

        void onValueChange(ValueView view, float value, String formatValue);
    }
}
