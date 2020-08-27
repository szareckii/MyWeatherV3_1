package com.geekbrains.myweatherv3;

import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;

public class Parcel implements Serializable {
    private String cityName;
    private boolean visibleWind;
    private boolean visiblePressure;
    private int countHoursBetweenForecasts;
    private boolean darkTheme;
    private ArrayList<String> data;
    private float lon;
    private float lat;
    private String currentFragmentName;

    public String getCurrentFragmentName() {
        return currentFragmentName;
    }

    public void setCurrentFragmentName(String currentFragmentName) {
        this.currentFragmentName = currentFragmentName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    public String getCityName() {
        return cityName;
    }

    public boolean isVisibleWind() {
        return visibleWind;
    }

    public boolean isVisiblePressure() {
        return visiblePressure;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public int getCountHoursBetweenForecasts() {
        return countHoursBetweenForecasts;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setVisibleWind(boolean visibleWind) {
        this.visibleWind = visibleWind;
    }

    public void setVisiblePressure(boolean visiblePressure) {
        this.visiblePressure = visiblePressure;
    }

    public void setCountHoursBetweenForecasts(int countHoursBetweenForecasts) {
        this.countHoursBetweenForecasts = countHoursBetweenForecasts;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public Parcel(String cityName, boolean visibleWind, boolean visiblePressure, int countHoursBetweenForecasts,
                  boolean darkTheme, ArrayList<String> data, float lon, float lat, String currentFragmentName) {
        this.cityName = cityName;
        this.visibleWind = visibleWind;
        this.visiblePressure = visiblePressure;
        this.countHoursBetweenForecasts = countHoursBetweenForecasts;
        this.darkTheme = darkTheme;
        this.data = data;
        this.lon = lon;
        this.lat = lat;
        this.currentFragmentName = currentFragmentName;
    }
}
