package com.geekbrains.myweatherv3.bd;

import android.util.Log;

import java.util.Date;
import java.util.List;

// Вспомогательный класс, развязывающий зависимость между Room и RecyclerView
public class WeatherSource {
    private final WeatherDao weatherDao;
    private static final String TAG = "myLogs";
//    private final CityWeatherDao cityWeatherDao;

    // Буфер с данными: сюда будем подкачивать данные из БД
    private List<City> cities;
    private List<CityWithHistory> cityWithHistory;

    public WeatherSource(WeatherDao weatherDao) {
        this.weatherDao = weatherDao;
    }

    // Получить все города
    public List<City> getCities() {
        // Если объекты еще не загружены, загружаем их.
        // Это сделано для того, чтобы не делать запросы к БД каждый раз
        if (cities == null){
            LoadCities();
        }
        return cities;
    }

    // Получить все города с погодой
    public List<CityWithHistory> getCitiesWithHistory() {
        if (cityWithHistory == null){
            LoadCitiesWithHistory();
        }
        return cityWithHistory;
    }

    // Загружаем города в буфер

    public void LoadCities(){
        cities = weatherDao.getAllCities();
    }

    private void LoadCitiesWithHistory() {
        cityWithHistory = weatherDao.getCityWithHistory();
    }

    // Получаем количество записей
    public long getCountCitiesWithHistory(){
        return weatherDao.getCountCitiesWithHistory();
    }

    // Получаем количество записей
    public long getCountCities(){
        return weatherDao.getCountCities();
    }

    // Добавляем город с погодой
    public void addCityWithWeather(City city, Date dateTemp, String temper) {
        //если новый город, то создаем запись для него. иначе - используем его id
        long id;
        Log.d(TAG, "getCityByName(city.cityName)  -  " + city.cityName);
        if ( weatherDao.getCityByName(city.cityName) == 0) {
            id = weatherDao.insertCity(city);
            Log.d(TAG, "!!!!weatherDao.insertCity(city): " + id);
        } else {
            id = weatherDao.getCityByName(city.cityName);
            Log.d(TAG, "!!!!weatherDao.insertCity(0): " + id);
        }

        HistoryWeather historyWeather1 = new HistoryWeather();
        historyWeather1.cityId = id;
        historyWeather1.dateTemp = dateTemp;
        historyWeather1.temper = temper;
        weatherDao.insertHistoryWeather(historyWeather1);

        // После изменения БД надо повторно прочесть данные из буфера
        LoadCities();
    }

}
