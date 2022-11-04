package com.example.mysensorapp;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RatingData {
    public float rating;
    public Timestamp time;
    private String TAG = "SaveData";
    private final String firebaseCollection = "RatingStats";

    public RatingData(float rating) {
        this.rating = rating;
        this.time = Timestamp.now();
    }

    // Save data to firebase database
    public void saveData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put("Date",time);
        data.put("Rating", rating);

        db.collection(firebaseCollection)
                .add(data)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }
}
