package com.example.mysensorapp;

import android.content.ContentResolver;
import android.provider.Settings;
import android.widget.TextView;

public class BrightnessListener extends Thread {
    private final ContentResolver cr;
    public float brightness;
    private boolean active;
    public TextView textView;

    public BrightnessListener(ContentResolver cr) {
        this.cr = cr;
    }

    @Override
    public void run() {
        active = true;
        while (active) {
            try {
                brightness = android.provider.Settings.System.getInt(cr, android.provider.Settings.System.SCREEN_BRIGHTNESS);
                if(textView != null)
                    textView.setText(String.valueOf(brightness));


            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public final void stopListener() {
        active = false;
    }
}
