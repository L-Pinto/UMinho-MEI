package com.example.mysensorapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.dynamicanimation.animation.SpringAnimation;

import android.content.IntentFilter;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;


import java.text.SimpleDateFormat;
import java.util.Date;

public class BackgroundService extends Service {
    private final IBinder binder = new LocalBinder();
    private final String channelId = "C1";
    private SensorManager sensorManager;

    // Listeners
    public LightListener lightListener;
    public BrightnessListener brightnessListener;
    public ScreenOnOffReceiver screenOnOffReceiver;

    // Dados para guardar na bd
    public AppData appData;


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        BackgroundService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BackgroundService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("BIND");
        startOnOffReceiver();
        return binder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }




    // Funcao invocada ao iniciar o servico
    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Start listeners here
        startLight();
        startBrightness();

        startForeground();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop all listeners here
        if(screenOnOffReceiver != null)
        {
            appData = new AppData(brightnessListener.brightness, lightListener.lightValue,false);
            appData.saveData();
            unregisterReceiver(screenOnOffReceiver);
            System.out.println("Service onDestroy: screenOnOffReceiver is unregistered.");
        }
        brightnessListener.stopListener();
        lightListener.stop();
    }

    public void startOnOffReceiver() {
        // Create an IntentFilter instance.
        IntentFilter intentFilter = new IntentFilter();
        // Add network connectivity change action.
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");


        // Set broadcast receiver priority.
        intentFilter.setPriority(100);
        // Create a network change broadcast receiver.
        screenOnOffReceiver = new ScreenOnOffReceiver();
        // Register the broadcast receiver with the intent filter object.
        registerReceiver(screenOnOffReceiver, intentFilter);
    }


    // Ativar LightListener
    public void startLight() {
        Sensor mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mLight != null) {
            lightListener = new LightListener(sensorManager);
            sensorManager.registerListener(lightListener, mLight,  SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // Ativar BrightnessListener
    public void startBrightness() {
        ContentResolver cr = getContentResolver();
        brightnessListener = new BrightnessListener(cr);
        brightnessListener.start();

    }


    // Criar uma notificacao
    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        createNotificationChannel();

        startForeground(1, new NotificationCompat.Builder(this,
                channelId) // don't forget create a notification channel first
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Classe que deteta quando o ecra esta ligado/desligado
    public class ScreenOnOffReceiver extends BroadcastReceiver {
        private final static String SCREEN_TOGGLE_TAG = "SCREEN_TOGGLE_TAG";
        public TextView screenOnView;

        public ScreenOnOffReceiver() {
            appData = new AppData(lightListener.lightValue, brightnessListener.brightness, true);
            appData.saveData();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                appData = new AppData(lightListener.lightValue, brightnessListener.brightness, false);
                appData.saveData();

                Log.d(SCREEN_TOGGLE_TAG, "Screen is turn off.");
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
                appData = new AppData(lightListener.lightValue, brightnessListener.brightness, true);
                screenOnView.setText(appData.getStringTime());
                appData.saveData();
                Log.d(SCREEN_TOGGLE_TAG, "Screen is turn on.");
            }
        }
    }
}
