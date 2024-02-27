package com.example.sensor;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.List;

public class BallDrawer extends View {


    double x = 0f;
    double y = 0f;
    List<double[]> line = new ArrayList<>();
    Coords coords;
    float speed;

    float[] orientation;
    int xy_rotation;
    int xz_rotation;
    int zy_rotation;

    private boolean isFirst = true;


    boolean movingRight = true;
    boolean movingTop = true;
    boolean movingBottom = true;
    boolean movingLeft = true;
    Paint paint = new Paint();

    private boolean isTouch = false;

    public void clearLine(boolean flag){
        if(line != null && flag){
            line.clear();
        }
    }

    public BallDrawer(Context context) {
        super(context);
    }

    public BallDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        if(isFirst){
            x = canvas.getWidth() / 2;
            y = canvas.getHeight() / 2;
            isFirst = false;
        }

        isTouch = MainActivity.isTouch;

        coords = MainActivity.coords;
        orientation = coords.getOrienation();
        xz_rotation = (int) Math.toDegrees(orientation[1]);
        zy_rotation = (int) Math.toDegrees(orientation[2]);

        speed = 8;
        paint.setColor(Color.RED);
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

        line.add(new double[]{x, y});

        clearLine(MainActivity.needClear);
        MainActivity.needClear = false;

        if(isTouch){

            if(xz_rotation > 0 && zy_rotation > 0 && movingTop && movingRight){
                x = x + (speed * cosAlpha);
                y = y - (speed * sinAlpha);
            } else if(xz_rotation > 0 && zy_rotation < 0 && movingTop && movingLeft){
                x = x - (speed * cosAlpha);
                y = y - (speed * sinAlpha);
            } else if(xz_rotation < 0 && zy_rotation < 0 && movingBottom && movingLeft){
                x = x - (speed * cosAlpha);
                y = y + (speed * sinAlpha);
            } else if(xz_rotation < 0 && zy_rotation > 0 && movingBottom && movingRight){
                x = x + (speed * cosAlpha);
                y = y + (speed * sinAlpha);
            } else if(xz_rotation == 0){
                if(zy_rotation > 0 && movingRight){
                    x = x + speed;
                } else if (zy_rotation < 0 && movingLeft){
                    x = x - speed;
                }
            } else if (zy_rotation == 0) {
                if(xz_rotation > 0 && movingTop){
                    y = y - speed;
                } else if(xz_rotation < 0 && movingBottom){
                    y = y + speed;
                }
            }
        }


        if(y <= 0){
            movingTop = false;
        } else {
            movingTop = true;
        }

        if (y >= canvas.getHeight() - size) {
            movingBottom = false;
        } else {
            movingBottom = true;
        }

        if (x <= 0) {
            movingLeft = false;
        } else{
            movingLeft = true;
        }

        if (x >= canvas.getWidth() - size) {
            movingRight = false;
        } else {
            movingRight = true;
        }



        for(double[] arr: line){
            canvas.drawCircle((float) arr[0] + size / 2, (float) arr[1] + size / 2,7, paint);
        }

        canvas.drawRect((float) x, (float) y, (float) (x + size), (float) (y + size), paint);

        invalidate();



    }

}
