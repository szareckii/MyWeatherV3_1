package com.geekbrains.myweatherv3.model;

public class Current {
    private float temp;
    private float wind_speed;
    private int pressure;
    private int wind_deg;
    private Weather[] weather;

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(float wind_speed) {
        this.wind_speed = wind_speed;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public int getWind_deg() {
        return wind_deg;
    }

    public void setWind_deg(int wind_deg) {
        this.wind_deg = wind_deg;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }
}
