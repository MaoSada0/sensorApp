package com.example.sensor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class PaintView extends View {
    public PaintView(Context context) {
        super(context);
    }

    private static int BRUSH_SIZE = 20;
    private static int COLOR = Color.RED;
    private static int BG_COLOR = Color.WHITE;
    private static float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    ArrayList<float[]> barrier;

    ////

    double x = 0f;
    double y = 0f;
    double x_next = 0f;
    double y_next = 0f;
    Coords coords;
    float speed;
    float[] orientation;

    int xz_rotation;
    int zy_rotation;
    private boolean isFirst = true;
    boolean movingRight = true;
    boolean movingTop = true;
    boolean movingBottom = true;
    boolean movingLeft = true;
    Paint paint = new Paint();

    private boolean isTouch = false;
    private boolean isDraw = false;
    private boolean isMotion = false;



    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
        barrier = new ArrayList<>();
    }

    public void init(DisplayMetrics metrics){
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    public void clear(){
        paths.clear();
        invalidate();
    }

    public void setDraw(){
        isDraw = !isDraw;
    }
    public void setMotion(){
        isMotion = !isMotion;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        mCanvas.drawColor(BG_COLOR);

        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);


            mCanvas.drawPath(fp.path, mPaint);
        }

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        canvas.restore();
        //////////////

        if(isFirst){
            x = canvas.getWidth() / 2;
            y = canvas.getHeight() / 2;
            isFirst = false;
        }

        coords = MainActivity.coords;
        orientation = coords.getOrienation();
        xz_rotation = (int) Math.toDegrees(orientation[1]);
        zy_rotation = (int) Math.toDegrees(orientation[2]);

        speed = 10;
        paint.setColor(Color.BLACK);
        Paint paint1 = new Paint();
        paint1.setStyle(Paint.Style.FILL);
        int size = 50;

        double alpha = 0;

        if(Math.abs(zy_rotation) > Math.abs(xz_rotation)){
            alpha = Math.abs(45 * (Math.abs(xz_rotation) / (double) Math.abs(zy_rotation)));
        } else if(Math.abs(zy_rotation) < Math.abs(xz_rotation)){
            alpha = Math.abs(90 - (45 * (Math.abs(zy_rotation) / (double) Math.abs(xz_rotation))));
        } else {
            alpha = 45;
        }
        Log.d("alpha", "" + alpha);

        alpha = Math.toRadians(alpha);
        double sinAlpha = Math.sin(alpha);
        double cosAlpha = Math.cos(alpha);

        if(isMotion){
            if(xz_rotation > 0 && zy_rotation > 0 && movingTop && movingRight){

                x_next = x + (speed * cosAlpha);
                y_next = y - (speed * sinAlpha);


                if(x_next >= canvas.getWidth() - size){
                    x_next = canvas.getWidth() - size;
                }

                if(y_next <= 0 + size){
                    y_next =  0 + size;
                }

                x = x_next;
                y = y_next;

            } else if(xz_rotation > 0 && zy_rotation < 0 && movingTop && movingLeft){
                x_next = x - (speed * cosAlpha);
                y_next = y - (speed * sinAlpha);

                if(x_next <= 0 + size){
                    x_next = 0 + size;
                }

                if(y_next <= 0 + size){
                    y_next =  0 + size;
                }

                x = x_next;
                y = y_next;

            } else if(xz_rotation < 0 && zy_rotation < 0 && movingBottom && movingLeft){
                x_next = x - (speed * cosAlpha);
                y_next = y + (speed * sinAlpha);

                if(x_next <= 0 + size){
                    x_next = 0 + size;
                }

                if(y_next >= canvas.getHeight() - size){
                    y_next =  canvas.getHeight() - size;
                }

                x = x_next;
                y = y_next;;
            } else if(xz_rotation < 0 && zy_rotation > 0 && movingBottom && movingRight){
                x_next = x + (speed * cosAlpha);
                y_next = y + (speed * sinAlpha);

                if(x_next >= canvas.getWidth() - size){
                    x_next = canvas.getWidth() - size;
                }

                if(y_next >= canvas.getHeight() - size){
                    y_next =  canvas.getHeight() - size;
                }

                x = x_next;
                y = y_next;;
            } else if(xz_rotation == 0){
                if(zy_rotation > 0 && movingRight){
                    x_next = x + speed;

                    if(x_next >= canvas.getWidth() - size){
                        x_next = canvas.getWidth() - size;
                    }

                    x = x_next;
                } else if (zy_rotation < 0 && movingLeft){
                    x_next = x - speed;

                    if(x_next <= 0 + size){
                        x_next = 0 + size;
                    }

                    x = x_next;
                }
            } else if (zy_rotation == 0) {
                if(xz_rotation > 0 && movingTop){
                    y_next = y - speed;

                    if(y_next <= 0 + size){
                        y_next =  0 + size;
                    }

                    y = y_next;
                } else if(xz_rotation < 0 && movingBottom){
                    y_next = y + speed;

                    if(y_next >= canvas.getHeight() - size){
                        y_next =  canvas.getHeight() - size;
                    }

                    y = y_next;;
                }
            }
        }



        canvas.drawCircle((float) x, (float) y, size, paint);
        //canvas.drawRect((float) x, (float) y, (float) (x + size), (float) (y + size), paint);

        invalidate();

    }

    private void touchStart(float x, float y){
        if(isDraw) {

            mPath = new Path();
            FingerPath fp = new FingerPath(COLOR, BRUSH_SIZE, mPath);
            paths.add(fp);

            mPath.reset();
            mPath.moveTo(x, y);

            mX = x;
            mY = y;
        }
    }

    private void touchMove(float x, float y){
        if(isDraw) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);

            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }
    }

    private void touchUp(){
        if(isDraw) {
            mPath.lineTo(mX, mY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        barrier.add(new float[]{x, y});
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                touchUp();
                invalidate();
                break;
        }


        return true;
    }
}
