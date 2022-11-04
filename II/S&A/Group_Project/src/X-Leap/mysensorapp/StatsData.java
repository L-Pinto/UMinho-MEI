package com.example.mysensorapp;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StatsData {
    private String statsCollection = "";
    private String TAG = "GetFirebaseData";

    public SleepStats sleepStats;
    public List<ActivityStats> activityStats;
    public List<RatingDayStats> ratingDayStats;
    public RatingCounterStats ratingCounterStats;


    public void getSleepStats() {
        statsCollection = "SleepStats";
        getData(0);
    }

    public void getActivityStats() {
        statsCollection = "ActivityStats";
        getData(1);
    }

    public void getRatingDayStats() {
        statsCollection = "RatingDayStats";
        getData(2);
    }

    public void getRatingCounterStats() {
        statsCollection = "RatingNumberStats";
        getData(3);
    }

    private void getData(int type) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(statsCollection).get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots.isEmpty()) {
                        Log.d(TAG, "onSuccess: LIST EMPTY");
                        return;
                    } else {
                        switch (type) {
                            case 0:
                                List<SleepStats> list = documentSnapshots.toObjects(SleepStats.class);
                                // Add all to your list
                                sleepStats = list.get(0);
                                Log.d(TAG, "onSuccess: " + sleepStats);
                                break;
                            case 1:
                                activityStats = documentSnapshots.toObjects(ActivityStats.class);
                                Collections.sort(activityStats, new Comparator<ActivityStats>() {
                                    @Override
                                    public int compare(ActivityStats o1, ActivityStats o2) {
                                        int comp = o1.getMonth() - o2.getMonth();
                                        if (comp == 0) {
                                            comp = o1.getDay() - o2.getDay();
                                        }
                                        return comp;
                                    }
                                });
                                Log.d(TAG, "onSuccess: " + activityStats);
                                break;
                            case 2:
                                ratingDayStats = documentSnapshots.toObjects(RatingDayStats.class);
                                Collections.sort(ratingDayStats, new Comparator<RatingDayStats>() {
                                    @Override
                                    public int compare(RatingDayStats o1, RatingDayStats o2) {
                                        int comp = o1.getMonth() - o2.getMonth();
                                        if (comp == 0) {
                                            comp = o1.getDay() - o2.getDay();
                                        }
                                        return comp;
                                    }
                                });
                                Log.d(TAG, "onSuccess: " + ratingDayStats);
                                break;
                            case 3:
                                List<RatingCounterStats> list1 = documentSnapshots.toObjects(RatingCounterStats.class);
                                ratingCounterStats = list1.get(0);
                                Log.d(TAG, "onSuccess: " + ratingCounterStats);
                                break;
                        }


                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }
}
