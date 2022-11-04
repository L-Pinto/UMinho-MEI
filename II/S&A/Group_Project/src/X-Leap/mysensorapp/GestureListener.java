package com.example.mysensorapp;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.core.view.GestureDetectorCompat;

public class GestureListener implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;
    public int tapCounter = 0;
    private TextView tapCounterView;


    public GestureListener() {}



    public void setListener(GestureDetectorCompat gl, TextView textView) {
        mDetector = gl;
        tapCounterView = textView;
        mDetector.setOnDoubleTapListener(this);
    }

    public void setListener(GestureDetectorCompat gl) {
        mDetector = gl;
        mDetector.setOnDoubleTapListener(this);
    }


    public boolean onTouchEvent(MotionEvent event){
        return this.mDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        //Log.d(DEBUG_TAG,"onDown: " + event.toString());

        return true;
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        //Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
        tapHandler();
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
        //Log.d(DEBUG_TAG, "onScroll: " + "X: " + Math.abs(distanceX)  + "Y: " + Math.abs(distanceY));
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        tapHandler();

        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        tapHandler();

        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }

    public void tapHandler() {
        tapCounter++;
        tapCounterView.setText(String.valueOf(tapCounter));
        //Log.d(DEBUG_TAG, "Toques: "+ tapCounter);
    }

    public void reset() {
        tapCounter = 0;
        tapCounterView.setText(String.valueOf(0));
    }

}
