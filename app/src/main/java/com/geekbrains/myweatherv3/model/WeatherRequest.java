package com.geekbrains.myweatherv3.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherRequest {
    @SerializedName("lat")
    @Expose
    private float lat;
    @SerializedName("lon")
    @Expose
    private float lon;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("timezone_offset")
    @Expose
    private String timezone_offset;
    @SerializedName("current")
    @Expose
    private Current current;
    @SerializedName("hourly")
    @Expose
    private Hourly[] hourly;
    @SerializedName("daily")
    @Expose
    private Daily[] daily;

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getTimezone_offset() {
        return timezone_offset;
    }

    public void setTimezone_offset(String timezone_offset) {
        this.timezone_offset = timezone_offset;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public Hourly[] getHourly() {
        return hourly;
    }

    public void setHourly(Hourly[] hourly) {
        this.hourly = hourly;
    }

    public Daily[] getDaily() {
        return daily;
    }

    public void setDaily(Daily[] daily) {
        this.daily = daily;
    }
}

