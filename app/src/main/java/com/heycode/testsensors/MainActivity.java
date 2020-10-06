package com.heycode.testsensors;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    Sensor accelerometer, magnetometer, proximity, lightSensor;
    TextView mTextView, sensor_name;

    float acc[] = new float[3];
    float mag[] = new float[3];
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text_view);
        sensor_name = findViewById(R.id.sensor_names);
        //getting services
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        root = findViewById(R.id.root_layout);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (accelerometer != null) {
            mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (proximity != null) {
            mSensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (lightSensor != null) {
            mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (magnetometer != null) {
            mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);//unregister all sensor
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        sensor_name.setText(sensorEvent.sensor.getName());

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                acc = sensorEvent.values;
                break;
            case Sensor.TYPE_PROXIMITY:
                //TODO::PROXI
                float [] pa = sensorEvent.values;
                if(pa[0]<5){
                    mTextView.setBackgroundColor(Color.WHITE);
                }else {
                    mTextView.setBackgroundColor(Color.CYAN);
                }
                break;
            case Sensor.TYPE_LIGHT:
                //TODO::LIGHT
                int grayShade = (int) sensorEvent.values[0];
                if (grayShade > 255) grayShade = 255;
//                mTextView.setBackgroundColor(Color.rgb(255 - grayShade, 255 - grayShade, 255 - grayShade));
                root.setBackgroundColor(Color.rgb(grayShade, grayShade, grayShade));
                mTextView.setTextColor(Color.RED);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mag = sensorEvent.values;
                break;
        }
        float [] rotation =  new float[9];
        boolean flag = mSensorManager.getRotationMatrix(rotation, null, acc, mag);
        if(flag){
            float [] orientationMat = new float[3];
            mSensorManager.getOrientation(rotation, orientationMat);

            float azimuthal = orientationMat[0];//angle between y axis and magnetic north
            float pitch = orientationMat[1];//z axis to tilt towards y axis
            float roll = orientationMat[2];//z axis to tilt towards x axis
            mTextView.setText(String.format("Azimuthal: %s\nPitch: %s\nRoll: %s", azimuthal, pitch, roll));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.bluetoothEx:
                startActivity(new Intent(MainActivity.this, BluetoothEx.class));
                break;

        }
        return true;
    }
}