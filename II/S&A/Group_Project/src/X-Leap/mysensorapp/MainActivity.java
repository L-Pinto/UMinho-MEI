package com.example.mysensorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    // Views
    private TextView brilho;
    private TextView tapCounterView;
    private TextView luz;
    private TextView screenOn;
    private RatingBar ratingBar;

    // Listeners
    private GestureListener gl;

    // Service vars
    BackgroundService bs;
    Intent intent;
    boolean mBound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            System.out.println("connect");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            bs = binder.getService();
            mBound = true;
            bs.lightListener.textView = luz;
            bs.screenOnOffReceiver.screenOnView = screenOn;
            String time = bs.appData.getStringTime();
            screenOn.setText(time);

            startBrightnessViewThread();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    // Função chamada ao iniciar a aplicacao
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        startGesture();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.item2):
                startActivity(new Intent(MainActivity.this, StatsActivity.class));
                return true;
            case(R.id.item3):
                startActivity(new Intent(MainActivity.this, LineChart_activityTime.class));
                return true;
            case(R.id.item4):
                startActivity(new Intent(MainActivity.this, LineChart_ratingday.class));
                return true;
            case(R.id.item5):
                startActivity(new Intent(MainActivity.this, Rating_StatsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Thread para mostrar os valores da brightness na view
    public void startBrightnessViewThread() {
        new Thread() {
            public void run() {
                while (mBound && bs != null && bs.brightnessListener != null) {
                    try {
                        runOnUiThread(() -> brilho.setText(String.valueOf(bs.brightnessListener.brightness)));
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
        unbindService(connection);

        mBound = false;
    }

    // Inicializar as views (MUDAR DE ACORDO COM A INTERFACE)
    public void initView() {
        setContentView(R.layout.activity_main);

        brilho = findViewById(R.id.valueBrightness);
        tapCounterView = findViewById(R.id.nTaps);
        luz = findViewById(R.id.valueLuminosity);
        screenOn = findViewById(R.id.valueOnOff);
        ratingBar = findViewById(R.id.ratingBar);

    }

    // Ativar/Desativar a recolha dos dados (invocada pelo botao da UI)
    public void activate(View view) {
        if (!mBound) {
            // Bind to BackgroundService
            intent = new Intent(this, BackgroundService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }
        else {
            stopService(intent);
            unbindService(connection);

            mBound = false;
        }
    }

    // Ativar listener de gestos
    public void startGesture() {
        // Detetor de toques no ecrã
        gl = new GestureListener();
        GestureDetectorCompat mDetector = new GestureDetectorCompat(this, gl);
        gl.setListener(mDetector, tapCounterView);
    }

    // Handler dos gestos
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mBound) {
            // Tipo de acao: 8 scroll,

            //System.out.println(event.getAction() + ": " + MotionEvent.actionToString(event.getAction()));
            if (gl != null && gl.onTouchEvent(event)) {
                return true;
            }
            return super.onTouchEvent(event);
        }
        return false;
    }

    public void trashButton(View view){
        gl.reset();
    }

    public void submitOnClick(View view) {
        float stars =  ratingBar.getRating();
        System.out.println("Total Stars:: " + ratingBar.getNumStars());
        System.out.println("Rating :: " + stars);
        RatingData rating = new RatingData(stars);
        rating.saveData();
        Toast.makeText(getApplicationContext(), "Rating: " + stars, Toast.LENGTH_LONG).show();
    }

    public void showMainOnClick(View view) {
        //setContentView(R.layout.activity_stats);
        startActivity(new Intent(this, MainActivity.class));
    }

    public void showSleepCycleOnClick(View view) {
        //setContentView(R.layout.activity_stats);
        startActivity(new Intent(MainActivity.this, StatsActivity.class));
    }


}