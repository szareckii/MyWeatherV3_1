package com.geekbrains.myweatherv3.bd;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = City.class,
        parentColumns = "id",
        childColumns = "city_id",
        onDelete = CASCADE),
        tableName = "history_weather")
public class HistoryWeather {
    @PrimaryKey(autoGenerate = true)
    public long id;

    // Внешний ключ
    @ColumnInfo(name = "city_id")
    public long cityId;

    //Дата поиска
    @ColumnInfo(name = "date_temp")
    public Date dateTemp;

    //Температура
    public String temper;
}
