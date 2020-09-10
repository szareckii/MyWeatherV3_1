package com.geekbrains.myweatherv3;

import android.graphics.drawable.Drawable;

public class DataClassOfDaysWithDrawable {
    String textDay;
    String texTemptDay;
    String texTemptNight;
    Drawable drawableDay;

    public DataClassOfDaysWithDrawable(String textDay, String texTemptDay, String texTemptNight, Drawable drawableDay) {
        this.textDay = textDay;
        this.texTemptDay = texTemptDay;
        this.texTemptNight = texTemptNight;
        this.drawableDay = drawableDay;
    }
}
