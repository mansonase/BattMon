package com.viseeointernational.battmon.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.viseeointernational.battmon.R;

public class RoundCornerImageView extends AppCompatImageView {

    private Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Path path = new Path();
    private Paint paint = new Paint();

    private float[] imageRadii = new float[8];
    private float[] borderRadii = new float[8];
    private RectF imageRect = new RectF();
    private RectF borderRect = new RectF();

    private int width;
    private int height;

    private int borderColor;
    private float borderWidth;
    private float cornerRadius;

    public RoundCornerImageView(Context context) {
        super(context);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerImageView);
        borderColor = typedArray.getColor(R.styleable.RoundCornerImageView_borderColor, Color.WHITE);
        borderWidth = typedArray.getDimension(R.styleable.RoundCornerImageView_borderWidth, 0);
        cornerRadius = typedArray.getDimension(R.styleable.RoundCornerImageView_cornerRadius, 0);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        for (int i = 0; i < borderRadii.length; i++) {
            borderRadii[i] = cornerRadius;
            imageRadii[i] = cornerRadius - borderWidth / 2f;
        }
        borderRect.set(borderWidth / 2f, borderWidth / 2f,
                width - borderWidth / 2f, height - borderWidth / 2f);
        imageRect.set(0, 0, width, height);
    }

    public void setBorderColor(int color) {
        this.borderColor = color;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.saveLayer(imageRect, null, Canvas.ALL_SAVE_FLAG);
        canvas.scale((width - 2 * borderWidth) / width,
                (height - 2 * borderWidth) / height,
                width / 2f,
                height / 2f);
        super.onDraw(canvas);

        paint.reset();
        path.reset();
        path.addRoundRect(imageRect, imageRadii, Path.Direction.CCW);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(xfermode);
        canvas.drawPath(path, paint);

        paint.setXfermode(null);
        canvas.restore();

        path.reset();
        paint.setStrokeWidth(borderWidth);
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        path.addRoundRect(borderRect, borderRadii, Path.Direction.CCW);
        canvas.drawPath(path, paint);
    }

}
