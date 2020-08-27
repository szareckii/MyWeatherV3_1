package com.geekbrains.myweatherv3;

import android.graphics.drawable.Drawable;

public class DataClassOfDays {
    String textDay;
    String texTemptDay;
    Drawable drawableDay;
    String texTemptNight;

    public DataClassOfDays(String textDay, String texTemptDay, Drawable drawableDay, String texTemptNight) {
        this.textDay = textDay;
        this.texTemptDay = texTemptDay;
        this.drawableDay = drawableDay;
        this.texTemptNight = texTemptNight;
    }
}
