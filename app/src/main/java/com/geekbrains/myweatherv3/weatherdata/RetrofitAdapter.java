package com.geekbrains.myweatherv3.weatherdata;

import com.geekbrains.myweatherv3.OpenWeather;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAdapter {
    public OpenWeather getOpenWeather() {
        return openWeather;
    }

    private OpenWeather openWeather;

    public RetrofitAdapter() {
        initRetorfit();
    }

    private OpenWeather initRetorfit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/") // Базовая часть адреса
                // Конвертер, необходимый для преобразования JSON в объекты
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Создаём объект, при помощи которого будем выполнять запросы
        openWeather = retrofit.create(OpenWeather.class);
        return openWeather;
    }
}
