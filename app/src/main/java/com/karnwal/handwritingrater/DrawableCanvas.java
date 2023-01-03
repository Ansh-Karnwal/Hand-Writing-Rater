package com.karnwal.handwritingrater;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import com.google.mlkit.vision.digitalink.Ink;

public class DrawableCanvas extends View {

    private Bitmap drawBitmap;
    private Canvas drawCanvas;
    private Ink.Builder inkBuilder = new Ink.Builder();
    private Ink.Stroke.Builder inkStrokeBuilder;
    private Path currentStroke = new Path();
    private float currentX = 0F;
    private float currentY = 0F;
    private float touchedX = 0F;
    private float touchedY = 0F;
    private long touchedTime = 0L;
    private int mTouchSlop;
    private Paint paint = new Paint();

    public DrawableCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(5f);
        paint.setDither(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        drawBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(drawBitmap);
        drawCanvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(drawBitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchedX = event.getX();
        touchedY = event.getY();
        touchedTime = System.currentTimeMillis();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                break;
        }
        return true;
    }

    private void touchStart() {
        currentStroke.reset();
        currentStroke.moveTo(touchedX, touchedY);
        currentX = touchedX;
        currentY = touchedY;
        inkStrokeBuilder = Ink.Stroke.builder();
        inkStrokeBuilder.addPoint(Ink.Point.create(touchedX, touchedY, touchedTime));
    }

    private void touchMove() {
        if ((Math.abs(touchedX - currentX) >= mTouchSlop) || (Math.abs(touchedY - currentY) >= mTouchSlop)) {
            currentStroke.quadTo(currentX, currentY, (touchedX + currentX) / 2, (touchedY + currentY) / 2);
            currentX = touchedX;
            currentY = touchedY;
            inkStrokeBuilder.addPoint(Ink.Point.create(touchedX, touchedY, touchedTime));
            drawCanvas.drawPath(currentStroke, paint);
        }
        invalidate();
    }

    private void touchUp() {
        inkStrokeBuilder.addPoint(Ink.Point.create(touchedX, touchedY, touchedTime));
        inkBuilder.addStroke(inkStrokeBuilder.build());
        currentStroke.reset();
    }

    protected void clear() {
        inkBuilder = Ink.builder();
        currentStroke.reset();
        drawCanvas.drawColor(Color.WHITE);
        invalidate();
    }

    protected Ink getInk() {
        return inkBuilder.build();
    }
}
