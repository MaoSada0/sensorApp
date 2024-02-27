package com.example.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

public class Coords implements SensorEventListener {

    Sensor mAccelerometer;
    Sensor mMagnetic;
    SensorManager mSensorManager;
    TextView xy;
    TextView xz;
    TextView zy;

    private boolean isTouch = false;
    public Coords(SensorManager mSensorManager, TextView[] tvs) {


        xy = tvs[0];
        xz = tvs[1];
        zy = tvs[2];


        this.mSensorManager = mSensorManager;

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    public Sensor getmMagnetic() {
        return mMagnetic;
    }

    public Sensor getmAccelerometer() {
        return mAccelerometer;
    }

    public void register(){
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_UI);
    }

    public void unRegister(){
        mSensorManager.unregisterListener(this);
    }

    public Coords(SensorManager mSensorManager) {

        this.mSensorManager = mSensorManager;

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    float[] accel = new float[3];
    float[] magnet = new float[3];

    float[] rotationMatrix = new float[16];
    float[] orienation = new float[3];

    public float[] getOrienation() {
        return orienation;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        loadSensorData(sensorEvent);
        SensorManager.getRotationMatrix(rotationMatrix, null, accel, magnet);
        SensorManager.getOrientation(rotationMatrix, orienation);
        orienation[2] = (float) (orienation[2] + (Math.PI / 90));
        orienation[1] = (float) (orienation[1] - (Math.PI / 180));
        isTouch = MainActivity.isTouch;

        if(isTouch && xy != null) {
            xy.setText(String.valueOf(Math.round(Math.toDegrees(orienation[0]))));
            xz.setText("(xz) y: " + String.valueOf(Math.round(Math.toDegrees(orienation[1]))));
            zy.setText("(zy) x: " + String.valueOf(Math.round(Math.toDegrees(orienation[2]))));
        }


    }

    private void loadSensorData(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accel = sensorEvent.values.clone();
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            magnet = sensorEvent.values.clone();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
