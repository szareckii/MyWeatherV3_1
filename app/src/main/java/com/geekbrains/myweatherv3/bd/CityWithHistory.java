package com.geekbrains.myweatherv3.bd;

import androidx.room.ColumnInfo;

import java.util.Date;

// Результат запроса к двум таблицам одновременно
public class CityWithHistory {

    // Название города
    @ColumnInfo(name = "city_name")
    public String cityName;

    //Дата поиска
    @ColumnInfo(name = "date_temp")
    public Date dateTemp;

    //Температура
    public String temper;

    // координата lon
    public float lon;

    // координата lat
    public float lat;

}
