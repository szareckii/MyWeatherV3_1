package com.geekbrains.myweatherv3;

import android.graphics.drawable.Drawable;

public class DataClassOfHours {
    String textHour;
    Drawable drawableHourImageView;
    String texTempHour;

    public DataClassOfHours(String textHour, Drawable drawableHourImageView, String texTempHour) {
        this.textHour = textHour;
        this.drawableHourImageView = drawableHourImageView;
        this.texTempHour = texTempHour;
    }
}
