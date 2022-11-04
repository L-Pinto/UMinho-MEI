package com.example.mysensorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class LightListener implements SensorEventListener {
    private final SensorManager sensorManager;
    public TextView textView;
    public float lightValue;

    public LightListener(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //System.out.println("LUZ");
        lightValue = sensorEvent.values[0];
        if (textView != null)
            textView.setText(String.valueOf(Math.round(lightValue)));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }
}
