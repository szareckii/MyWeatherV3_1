package com.geekbrains.myweatherv3;

import java.io.Serializable;

public class DataClassOfHours implements Serializable {
    String textHour;
    String drawableHourImageView;
    String texTempHour;

    public DataClassOfHours(String textHour, String drawableHourImageView, String texTempHour) {
        this.textHour = textHour;
        this.drawableHourImageView = drawableHourImageView;
        this.texTempHour = texTempHour;
    }
}
