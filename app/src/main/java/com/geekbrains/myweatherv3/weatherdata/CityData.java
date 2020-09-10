package com.geekbrains.myweatherv3.weatherdata;

import android.util.Log;
import com.geekbrains.myweatherv3.BuildConfig;
import com.geekbrains.myweatherv3.Parcel;
import com.geekbrains.myweatherv3.model.SearchRequest;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class CityData {
    private Parcel parcel;
    private static final String TAG = "myLogs";
    private static final String WEATHER_URL_FOR_SEARCH = "https://api.openweathermap.org/data/2.5/weather?lang=ru&q=";
    private static final String WEATHER_SET_API_KEY_FOR_SEARCH = "&appid=";

    public CityData(Parcel parcel) {
        this.parcel = parcel;
    }

    /*Поиск города на сайте погоды*/
    public void findCityOnOpenweathermap(String cityName) {
        try {
            final URL uri = new URL(WEATHER_URL_FOR_SEARCH + cityName +
                    WEATHER_SET_API_KEY_FOR_SEARCH + BuildConfig.WEATHER_API_KEY);
            Log.e(TAG, "URI: " + uri);
            new Thread(() -> {
                HttpsURLConnection urlConnection = null;
                try {
                    /*Настройки дла соединения с ПРОКСИ*/
//                        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.10.10.10", 123));
//                        urlConnection = (HttpsURLConnection) uri.openConnection(proxy);
                    urlConnection = (HttpsURLConnection) uri.openConnection();

                    urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
                    urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
                    Log.e(TAG, "Connect: true");
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
                    String result = getLines(in);
                    // преобразование данных запроса в модель
                    Log.e(TAG, "getLines() result: true");
                    Gson gson = new Gson();
                    final SearchRequest searchRequest = gson.fromJson(result, SearchRequest.class);
                    new CityDataOnlyNeed(parcel, searchRequest);
                } catch (Exception e) {
                    Log.e(TAG, "Fail connection", e);
                    parcel.setRequestFlag(-1);
                    e.printStackTrace();
                } finally {
                    if (null != urlConnection) {
                        urlConnection.disconnect();
                    }
                }
            }).start();
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

}
