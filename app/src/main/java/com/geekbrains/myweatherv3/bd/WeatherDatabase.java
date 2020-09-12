package com.geekbrains.myweatherv3.bd;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {City.class, HistoryWeather.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract WeatherDao getWeatherDao();
}
