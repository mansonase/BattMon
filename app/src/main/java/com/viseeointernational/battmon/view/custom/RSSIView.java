package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.viseeointernational.battmon.R;

public class RSSIView extends View {

    private static final int COLOR_GRAY = Color.parseColor("#66000000");

    private int rssi;
    private int color = Color.WHITE;

    private Paint paint = new Paint();
    private int width;
    private int height;
    private float lineWidth;
    private float circleRadius;

    public RSSIView(Context context) {
        super(context);
    }

    public RSSIView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RSSIView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public RSSIView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RSSIView);
        color = typedArray.getColor(R.styleable.RSSIView_color, Color.WHITE);
        rssi = typedArray.getInteger(R.styleable.RSSIView_rssi, 0);
        lineWidth = typedArray.getDimension(R.styleable.RSSIView_lineWidth, 6);
        circleRadius= typedArray.getDimension(R.styleable.RSSIView_circleRadius,20);
        typedArray.recycle();
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);

        if (rssi < -105) {
            paint.setColor(COLOR_GRAY);
        } else {
            paint.setColor(color);
        }
        //canvas.drawCircle(width / 2f, height - lineWidth / 2f, lineWidth / 2f, paint);
        canvas.drawCircle(width / 2f, height - circleRadius, circleRadius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        if (rssi < -60) {
            paint.setColor(COLOR_GRAY);
        } else {
            paint.setColor(color);
        }
        canvas.drawArc(new RectF(0, lineWidth / 2f, width, height),
                -135, 90, false, paint);

        if (rssi < -75) {
            paint.setColor(COLOR_GRAY);
        } else {
            paint.setColor(color);
        }
        canvas.drawArc(new RectF(width / 6f, (height - lineWidth) / 3f + lineWidth / 2f, width - width / 6f, height),
                -135, 90, false, paint);

        if (rssi < -90) {
            paint.setColor(COLOR_GRAY);
        } else {
            paint.setColor(color);
        }
        canvas.drawArc(new RectF(width / 3f, (height - lineWidth) / 3f * 2 + lineWidth / 2f, width - width / 3f, height),
                -135, 90, false, paint);
    }
}
