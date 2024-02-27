package com.example.sensor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Sensor mAccelerometer;
    Sensor mMagnetic;
    SensorManager mSensorManager;

    TextView xy;
    TextView xz;
    TextView zy;
    static Coords coords;
    static boolean needClear = false;

    Button clear_btn;
    Button draw_btn;
    Button motion_btn;

    static boolean isTouch = true;


    private PaintView paintView;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        xy = findViewById(R.id.xy);
        xz = findViewById(R.id.xz);
        zy = findViewById(R.id.zy);

        clear_btn = findViewById(R.id.clear);
        draw_btn = findViewById(R.id.can_draw);
        motion_btn = findViewById(R.id.motion);

        coords = new Coords(mSensorManager, new TextView[]{xy, xz, zy});

        mAccelerometer = coords.getmAccelerometer();
        mMagnetic = coords.getmMagnetic();

        Log.d("list", (mSensorManager.getSensorList(Sensor.TYPE_ALL)).toString());

        paintView = findViewById(R.id.pw);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        paintView.init(displayMetrics);

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               paintView.clear();
            }
        });

        draw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setDraw();
            }
        });

        motion_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setMotion();
            }
        });

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        coords.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        coords.unRegister();
    }


}