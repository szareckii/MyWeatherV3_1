package com.geekbrains.myweatherv3;

import com.geekbrains.myweatherv3.model.SearchRequest;
import com.geekbrains.myweatherv3.model.WeatherRequest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeather {

    @GET("data/2.5/onecall")
    Call<WeatherRequest> loadWeather(@Query("exclude") String exclude, @Query("units") String units,
                                     @Query("appid") String keyApi, @Query("lat") float lat,
                                     @Query("lon") float lon);

    @GET("data/2.5/weather")
    Call<SearchRequest> loadCityCoord(@Query("q") String cityCountry, @Query("appid") String keyApi,
                                      @Query("lang") String lang);
}
