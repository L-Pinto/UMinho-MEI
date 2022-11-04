package com.example.mysensorapp;

public class SleepStats {
    public int day;
    public int month;
    public float lightsleep;
    public float deepsleep;
    public float rem;
    public float awake;

    public SleepStats() {
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public float getLightsleep() {
        return lightsleep;
    }

    public void setLightsleep(float lightsleep) {
        this.lightsleep = lightsleep;
    }

    public float getDeepsleep() {
        return deepsleep;
    }

    public void setDeepsleep(float deepsleep) {
        this.deepsleep = deepsleep;
    }

    public float getRem() {
        return rem;
    }

    public void setRem(float rem) {
        this.rem = rem;
    }

    public float getAwake() {
        return awake;
    }

    public void setAwake(float awake) {
        this.awake = awake;
    }

    @Override
    public String toString() {
        return "SleepData{" +
                "lightsleep=" + lightsleep +
                ", deepsleep=" + deepsleep +
                ", rem=" + rem +
                ", awake=" + awake +
                '}';
    }
}
