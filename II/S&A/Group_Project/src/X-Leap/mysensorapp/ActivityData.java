package com.example.mysensorapp;

import com.google.firebase.Timestamp;

public class ActivityData {
    public boolean onOff;
    public Timestamp time;

    public ActivityData(boolean onOff) {
        this.onOff = onOff;
        this.time = Timestamp.now();
    }

    public String getStringTime() {
        String timeString = time.toDate().toString();
        String[] data = timeString.split(" ");

        return data [3] + " (" + data[2] + " " + data[1] + ")";
    }
}
