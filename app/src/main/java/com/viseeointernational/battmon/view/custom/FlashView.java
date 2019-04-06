package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.viseeointernational.battmon.R;

public class FlashView extends View {

    public FlashView(Context context) {
        super(context);
    }

    public FlashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FlashView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public FlashView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlashView);
        color = typedArray.getColor(R.styleable.FlashView_flashColor, Color.GREEN);
        radius = typedArray.getDimension(R.styleable.FlashView_flashRadius, 30);
        int speed = typedArray.getInteger(R.styleable.FlashView_breatheSpeed, 1500);
        breathe = typedArray.getBoolean(R.styleable.FlashView_breathe, false);
        typedArray.recycle();
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        paint.setMaskFilter(new BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL));
        step = (ALPHA_MAX - ALPHA_MIN) / (speed / 2f / 16f);
        alpha = ALPHA_MAX;
        brighten = false;
        setPaintParameter(color, alpha);
    }

    private static final float ALPHA_MAX = 200f;
    private static final float ALPHA_MIN = 30f;

    private int color;
    private float radius;
    private boolean breathe;
    private float alpha;
    private boolean brighten;
    private float step;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectF;

    public void enableBreathe(boolean enable) {
        breathe = enable;
        alpha = ALPHA_MAX;
        brighten = false;
        setPaintParameter(color, alpha);
        invalidate();
    }

    public void setColor(int color) {
        this.color = color;
        alpha = ALPHA_MAX;
        brighten = false;
        setPaintParameter(color, alpha);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF = new RectF(radius, radius, w - radius, h - radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(rectF, paint);
        if (!breathe) {
            return;
        }
        if (brighten) {
            alpha += step;
            if (alpha >= ALPHA_MAX) {
                brighten = false;
            }
        } else {
            alpha -= step;
            if (alpha <= ALPHA_MIN) {
                brighten = true;
            }
        }
        setPaintParameter(color, alpha);
        postInvalidateDelayed(16);
    }

    private void setPaintParameter(int color, float alpha) {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        paint.setColor(Color.argb((int) alpha, red, green, blue));
    }
}
