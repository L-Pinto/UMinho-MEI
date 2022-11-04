package com.example.mysensorapp;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AppData {
    public float light;
    public float brightness;
    public ActivityData activityData;
    private String TAG = "SaveData";
    private final String firebaseCollection = "UsageStats";

    public AppData(float light, float brightness, boolean on) {
        this.light = light;
        this.brightness = brightness;
        activityData = new ActivityData(on);
    }

    public String getStringTime() {
        return activityData.getStringTime();
    }

    // Save data to firebase database
    public void saveData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("Date",activityData.time);
        data.put("ScreenOn",activityData.onOff);
        data.put("Light", light);
        data.put("Brightness", brightness);


        db.collection(firebaseCollection)
                .add(data)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));


    }


}
