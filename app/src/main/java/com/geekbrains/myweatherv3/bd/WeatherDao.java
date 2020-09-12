package com.geekbrains.myweatherv3.bd;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface WeatherDao {
    // Метод для добавления города в базу данных
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCity(City city);

    // Удаляем данные города, зная ключ
    @Query("DELETE FROM city WHERE id = :id")
    void deleteStudentById(long id);

    // Забираем данные по всем городам
    @Query("SELECT * FROM city")
    List<City> getAllCities();

    // Получаем данные одного города по id
    @Query("SELECT * FROM city WHERE id = :id")
    City getCityById(long id);

    // Получаем данные одного города по наименованию
    @Query("SELECT id FROM city WHERE city_name = :cityName")
    int getCityByName(String cityName);

    //Получаем количество записей в таблице
    @Query("SELECT COUNT() FROM city")
    long getCountCities();

    //Получаем количество записей в таблицах
    @Query("SELECT count() " +
            "FROM city " +
            "INNER JOIN history_weather ON city.id = history_weather.city_id")
    long getCountCitiesWithHistory();

    // Метод для добавления погоды в базу данных
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistoryWeather(HistoryWeather historyWeather);

    @Query("SELECT city.city_name, city.lon, city.lat, history_weather.date_temp, history_weather.temper " +
            "FROM city " +
            "INNER JOIN history_weather ON city.id = history_weather.city_id")
    List<CityWithHistory> getCityWithHistory();

}

