package com.geekbrains.myweatherv3.weatherdata;

import android.util.Log;
import com.geekbrains.myweatherv3.BuildConfig;
import com.geekbrains.myweatherv3.Parcel;
import com.geekbrains.myweatherv3.model.WeatherRequest;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class WeatherData {
    private Parcel parcel;
    private static final String TAG = "myLogs";
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/onecall?exclude=minutely&units=metric&appid=";
    private static String WEATHER_URL_CITY;

    public WeatherData(Parcel parcel) {
        this.parcel = parcel;
    }

    /*Метод получения погоды из JSON"*/
    public void setWhether() {
        try {
            WEATHER_URL_CITY = String.format(Locale.getDefault(), "&lat=%f&lon=%f", parcel.getLat(), parcel.getLon());
            WEATHER_URL_CITY = getGeoFromCityName(parcel.getLat(), parcel.getLon());
            final URL uri = new URL(WEATHER_URL + BuildConfig.WEATHER_API_KEY + WEATHER_URL_CITY);
            Log.e(TAG, "URI: " + uri);
                HttpsURLConnection urlConnection = null;
                try {
                    /*Настройки дла соединения с ПРОКСИ*/
//                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.10.10.10", 123));
//                    urlConnection = (HttpsURLConnection) uri.openConnection(proxy);
                    urlConnection = (HttpsURLConnection) uri.openConnection();

                    urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
                    urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
                    Log.e(TAG, "Connect: true");
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
                    String result = getLines(in);
                    // преобразование данных запроса в модель
                    Log.e(TAG, "getLines() result: true");
                    Gson gson = new Gson();
                    final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
                    // Возвращаемся к основному потоку
                    new WeatherDataOnlyNeed(parcel, weatherRequest);
                    Log.e(TAG, "new WeatherDataOnlyNeed(parcel, weatherRequest, context)");
                } catch (Exception e) {
                    Log.e(TAG, "Fail connection", e);
                    parcel.setRequestFlag(-1);
                    e.printStackTrace();
                } finally {
                    if (null != urlConnection) {
                        urlConnection.disconnect();
                    }
                }
//            }).start();
        } catch (MalformedURLException e) {
            Log.e(TAG, "Fail URI", e);
            e.printStackTrace();
        }
    }

    private String getLines(BufferedReader in) {

        StringBuilder rawData = new StringBuilder(1024);
        String tempVariable;

        while (true) {
            try {
                tempVariable = in.readLine();
                if (tempVariable == null) break;
                rawData.append(tempVariable).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rawData.toString();
    }

    /*Метод получения локкации города по ему имени (на openweathermap погода за 7 дней ищется только
                         по координатам)*/
    private String getGeoFromCityName(float lat, float lon) {
        if (lat != 0.0f) {
            WEATHER_URL_CITY = "&lat=" + lat + "&lon=" + lon;
        }

        switch (parcel.getCityName()) {
            case "Moscow":
            case "Москва":
                WEATHER_URL_CITY = "&lat=55.75&lon=37.62";
                break;
            case "London":
            case "Лондон":
                WEATHER_URL_CITY = "&lat=51.50853&lon=-0.12574";
                break;
            case "New York":
            case "Нью-Йорк":
                WEATHER_URL_CITY = "&lat=43.000351&lon=-75.499901";
                break;
            case "Beijing":
            case "Пекин":
                WEATHER_URL_CITY = "&lat=39.907501&lon=116.397232";
                break;
            case "Paris":
            case "Париж":
                WEATHER_URL_CITY = "&lat=48.853401&lon=2.3486";
                break;
        }
        return WEATHER_URL_CITY;
    }

}
