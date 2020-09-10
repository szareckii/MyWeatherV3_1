package com.geekbrains.myweatherv3;

import android.graphics.drawable.Drawable;

public class DataClassOfHoursWithDrawable {
    String textHour;
    Drawable drawableHourImageView;
    String texTempHour;

    public DataClassOfHoursWithDrawable(String textHour, Drawable drawableHourImageView, String texTempHour) {
        this.textHour = textHour;
        this.drawableHourImageView = drawableHourImageView;
        this.texTempHour = texTempHour;
    }
}
