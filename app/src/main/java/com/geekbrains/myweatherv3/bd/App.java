package com.geekbrains.myweatherv3.bd;

import android.app.Application;

import androidx.room.Room;

public class App extends Application {

    private static App instance;

    // База данных
    private WeatherDatabase db;

    // Получаем объект приложения
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Сохраняем объект приложения (для Singleton’а)
        instance = this;

        // Строим базу
        db = Room.databaseBuilder(
                getApplicationContext(),
                WeatherDatabase.class,
                "weather_database.db")
//                .allowMainThreadQueries() //Только для примеров и тестирования.
                .build();
    }

    // Получаем EducationDao для составления запросов
    public WeatherDao getWeatherDao() {
        return db.getWeatherDao();
    }
}

