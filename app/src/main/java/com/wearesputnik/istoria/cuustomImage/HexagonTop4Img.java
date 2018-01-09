package com.wearesputnik.istoria.cuustomImage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by admin on 27.10.17.
 */

public class HexagonTop4Img extends android.support.v7.widget.AppCompatImageView {
    private Path hexagonPath;
    private Path hexagonBorderPath;
    private Paint mBorderPaint;

    public HexagonTop4Img(Context context) {
        super(context);
        init();
    }

    public HexagonTop4Img(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HexagonTop4Img(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.hexagonPath = new Path();
        this.hexagonBorderPath = new Path();

        this.mBorderPaint = new Paint();
        this.mBorderPaint.setColor(Color.RED);
        this.mBorderPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mBorderPaint.setStrokeWidth(15f);
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
        this.mBorderPaint.setShadowLayer(10.0f, 5.0f, 2.0f, Color.RED);
    }

    public void setRadius(float radius) {
        calculatePath(radius);
    }

    public void setBorderColor(int color) {
        this.mBorderPaint.setColor(color);
        this.mBorderPaint.setShadowLayer(10.0f, 5.0f, 2.0f, color);
        invalidate();
    }

    private void calculatePath(float radius) {
        float halfRadius = radius / 2f;
        float triangleHeight = (float) (Math.sqrt(3.0) * halfRadius);
        float centerX = getMeasuredWidth() / 2f;
        float centerY = getMeasuredHeight() / 2f;

        this.hexagonPath.reset();
        this.hexagonPath.moveTo(centerX, centerY - radius);
        this.hexagonPath.lineTo(centerX + triangleHeight, centerY - halfRadius);
        this.hexagonPath.lineTo(centerX - triangleHeight, centerY + halfRadius);
        this.hexagonPath.lineTo(centerX - triangleHeight, centerY - halfRadius);
        this.hexagonPath.close();

        float radiusBorder = radius - 5f;
        float halfRadiusBorder = radiusBorder / 2f;
        float triangleBorderHeight = (float) (Math.sqrt(3.0) * halfRadiusBorder);

        this.hexagonBorderPath.reset();
        this.hexagonBorderPath.moveTo(centerX, centerY - radiusBorder);
        this.hexagonBorderPath.lineTo(centerX + triangleBorderHeight - 6, centerY - halfRadiusBorder - 2);
        this.hexagonBorderPath.lineTo(centerX - triangleBorderHeight, centerY + halfRadiusBorder - 6);
        this.hexagonBorderPath.lineTo(centerX - triangleBorderHeight, centerY - halfRadiusBorder);
        this.hexagonBorderPath.close();
        invalidate();
    }

    @Override
    public void onDraw(Canvas c) {
        c.drawPath(hexagonBorderPath, mBorderPaint);
        c.clipPath(hexagonPath, Region.Op.INTERSECT);
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        super.onDraw(c);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        calculatePath(Math.min(width / 2f, height / 2f) - 10f);
    }
}