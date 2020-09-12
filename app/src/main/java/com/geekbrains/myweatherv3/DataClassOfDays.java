package com.geekbrains.myweatherv3;

import java.io.Serializable;

public class DataClassOfDays implements Serializable {
    String textDay;
    String texTemptDay;
    String drawableDay;
    String texTemptNight;

    public DataClassOfDays(String textDay, String texTemptDay, String drawableDay, String texTemptNight) {
        this.textDay = textDay;
        this.texTemptDay = texTemptDay;
        this.drawableDay = drawableDay;
        this.texTemptNight = texTemptNight;
    }
}
