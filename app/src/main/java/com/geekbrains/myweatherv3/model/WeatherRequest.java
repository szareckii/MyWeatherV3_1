package com.geekbrains.myweatherv3.model;

public class WeatherRequest {
    private Current current;
    private Hourly[] hourly;
    private Daily[] daily;

    public Current getCurrent() {
        return current;
    }

    public Daily[] getDaily() {
        return daily;
    }

    public Hourly[] getHourly() {
        return hourly;
    }
}

