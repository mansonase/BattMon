package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import com.viseeointernational.battmon.R;

public class WaveView extends View {

    private int width;
    private int height;
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint transparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path path = new Path();
    private RectF transparentRectF;

    private PointF startPoint;

    private int color;
    private float alpha;
    private float length;// 周期长度
    private float amplitude;// 振幅
    private float speed;// 速度
    private float progress;

    public WaveView(Context context) {
        super(context);
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        color = typedArray.getColor(R.styleable.WaveView_waveColor, Color.GREEN);
        alpha = typedArray.getFloat(R.styleable.WaveView_alpha, 0.6f);
        length = typedArray.getDimension(R.styleable.WaveView_length, 400);
        amplitude = typedArray.getDimension(R.styleable.WaveView_amplitude, 14);
        speed = typedArray.getDimension(R.styleable.WaveView_speed, 2);
        progress = typedArray.getFloat(R.styleable.WaveView_progress, 0);
        typedArray.recycle();
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    public void setColor(int color) {
        this.color = color;
        setPaintParameter(progress, color, alpha);
        invalidate();
    }

    public void setProgress(float progress) {
        this.progress = progress;
        setPaintParameter(progress, color, alpha);
        invalidate();
    }

    private void setPaintParameter(float progress, int color, float alpha) {
        startPoint = new PointF(width / 100f * progress, -length);

        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        int a = (int) (alpha * 255f + 0.5f);
        backgroundPaint.setColor(Color.argb(a, red, green, blue));

        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        transparentPaint.setColor(Color.TRANSPARENT);

        float padding = height / 10f;
        transparentPaint.setMaskFilter(new BlurMaskFilter(padding * 3f, BlurMaskFilter.Blur.NORMAL));
        transparentRectF = new RectF(padding, padding, startPoint.x - padding, height - padding);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        setPaintParameter(progress, color, alpha);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int count = canvas.saveLayer(0, 0, getWidth(), getHeight(), backgroundPaint, Canvas.ALL_SAVE_FLAG);

        // 画背景
        path.moveTo(startPoint.x, startPoint.y);// 右上角
        for (int i = 0; i < height / length + 2; i++) {
            path.quadTo(startPoint.x + amplitude, startPoint.y + (length * (i + 0.25f)),
                    startPoint.x, startPoint.y + length * (i + 0.5f));
            path.quadTo(startPoint.x - amplitude, startPoint.y + (length * (i + 0.75f)),
                    startPoint.x, startPoint.y + length * (i + 1));
        }
        path.lineTo(0, height);// 左下角
        path.lineTo(0, startPoint.y);// 左上角
        path.close();
        canvas.drawPath(path, backgroundPaint);

        if (transparentRectF.right - transparentRectF.left > 0) {
            // 画透明部分
            canvas.drawRoundRect(transparentRectF, transparentRectF.left, transparentRectF.left, transparentPaint);
            canvas.restoreToCount(count);
        }

        if (startPoint.y >= 0) {
            startPoint.y -= length;
        }
        startPoint.y += speed;
        path.reset();
        postInvalidateDelayed(16);
    }
}
