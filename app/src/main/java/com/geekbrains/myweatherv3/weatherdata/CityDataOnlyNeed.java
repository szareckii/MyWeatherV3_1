package com.geekbrains.myweatherv3.weatherdata;

import android.util.Log;
import com.geekbrains.myweatherv3.Parcel;
import com.geekbrains.myweatherv3.model.SearchRequest;

import java.util.ArrayList;

import retrofit2.Response;

public class CityDataOnlyNeed {
    private static final String TAG = "myLogs";
    private Parcel parcel;

    public CityDataOnlyNeed(Parcel parcel, Response<SearchRequest> response) {
        this.parcel = parcel;
        searchCity(response);
    }

    private void searchCity(Response<SearchRequest> response) {
        assert response.body() != null;
        float lon = response.body().getCoord().getLon();
        float lat = response.body().getCoord().getLat();
        Log.e(TAG, "CityCoordinates. Coord: lon - " + lon + " lat: " + lat);
        Log.e(TAG, "requestFlag: 1");
        ArrayList<String> listData = parcel.getDataCites();
        listData.add(response.body().getName());
        parcel.setDataCites(listData);
        parcel.setCityName(response.body().getName());
        parcel.setLat(lat);
        parcel.setLon(lon);
        parcel.setRequestFlag(1);
    }
}
