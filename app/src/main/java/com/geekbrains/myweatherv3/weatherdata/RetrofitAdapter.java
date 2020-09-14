package com.geekbrains.myweatherv3.weatherdata;

import com.geekbrains.myweatherv3.OpenWeather;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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

        //Создаем лог  ретрофита
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/") // Базовая часть адреса
                // Конвертер, необходимый для преобразования JSON в объекты
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        // Создаём объект, при помощи которого будем выполнять запросы
        openWeather = retrofit.create(OpenWeather.class);
        return openWeather;
    }
}
