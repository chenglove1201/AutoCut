package com.cheng.autocut.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ICAN on 2018/1/3.
 */

public class AreaView extends View {
    private Paint paint;
    private Point[] points;
    private Path path;
    private int width = 400;
    private int height = 200;
    private Bitmap bitmap;

    public AreaView(Context context) {
        this(context, null);
    }

    public AreaView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AreaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setAlpha(150);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (points != null && points.length == 4) {
            path.moveTo(point2ViewX(points[0]), point2ViewY(points[0]));
            path.lineTo(point2ViewX(points[1]), point2ViewY(points[1]));
            path.lineTo(point2ViewX(points[2]), point2ViewY(points[2]));
            path.lineTo(point2ViewX(points[3]), point2ViewY(points[3]));
            canvas.drawPath(path, paint);
        }
    }

    public void updateArea(Point[] points, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.points = points;
        path.reset();
        invalidate();
    }

    private float point2ViewX(Point point) {
        return (float) point.x / bitmap.getWidth() * width;
    }

    private float point2ViewY(Point point) {
        return (float) point.y / bitmap.getHeight() * height;
    }
}
