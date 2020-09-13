package com.geekbrains.myweatherv3;

import java.io.Serializable;
import java.util.ArrayList;

public class Parcel implements Serializable {
    private String cityName;
    private String tempCurrent;
    private String windNow;
    private String pressureNow;
    private int windDegree;
    private String typesWeather;
    private DataClassOfHours[] dataHours;
    private DataClassOfDays[] dataDays;
    private boolean visibleWind;
    private boolean visiblePressure;
    private int countHoursBetweenForecasts;
    private boolean darkTheme;
    private ArrayList<String> dataCites;
    private float lon;
    private float lat;
    private String currentFragmentName;

    public DataClassOfHours[] getDataHours() {
        return dataHours;
    }

    public void setDataHours(DataClassOfHours[] dataHours) {
        this.dataHours = dataHours;
    }

    public DataClassOfDays[] getDataDays() {
        return dataDays;
    }

    public void setDataDays(DataClassOfDays[] dataDays) {
        this.dataDays = dataDays;
    }


    public String getCurrentFragmentName() {
        return currentFragmentName;
    }

    public void setCurrentFragmentName(String currentFragmentName) {
        this.currentFragmentName = currentFragmentName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setDataCites(ArrayList<String> dataCites) {
        this.dataCites = dataCites;
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

    public ArrayList<String> getDataCites() {
        return dataCites;
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
        this.dataCites = data;
        this.lon = lon;
        this.lat = lat;
        this.currentFragmentName = currentFragmentName;
    }

    public String getTempCurrent() {
        return tempCurrent;
    }

    public void setTempCurrent(String tempCurrent) {
        this.tempCurrent = tempCurrent;
    }

    public String getWindNow() {
        return windNow;
    }

    public void setWindNow(String windNow) {
        this.windNow = windNow;
    }

    public String getPressureNow() {
        return pressureNow;
    }

    public void setPressureNow(String pressureNow) {
        this.pressureNow = pressureNow;
    }

    public int getWindDegree() {
        return windDegree;
    }

    public void setWindDegree(int windDegree) {
        this.windDegree = windDegree;
    }

    public String getTypesWeather() {
        return typesWeather;
    }

    public void setTypesWeather(String typesWeather) {
        this.typesWeather = typesWeather;
    }
}
