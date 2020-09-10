package com.geekbrains.myweatherv3.bd;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = "city_name")},
        tableName = "city")
public class City {

    @PrimaryKey(autoGenerate = true)
    public long id;

    // Название города
    @ColumnInfo(name = "city_name")
    public String cityName;

    // координата lon
    public float lon;

    // координата lat
    public float lat;

}

