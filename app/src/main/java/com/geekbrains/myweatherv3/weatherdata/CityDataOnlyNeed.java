package com.geekbrains.myweatherv3.weatherdata;

import android.util.Log;
import com.geekbrains.myweatherv3.Parcel;
import com.geekbrains.myweatherv3.model.SearchRequest;

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
        parcel.setCityName(response.body().getName());
        parcel.setLat(lat);
        parcel.setLon(lon);
    }
}
