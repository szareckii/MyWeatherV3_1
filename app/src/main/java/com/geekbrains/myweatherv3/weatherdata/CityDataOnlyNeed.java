package com.geekbrains.myweatherv3.weatherdata;

import android.util.Log;
import com.geekbrains.myweatherv3.Parcel;
import com.geekbrains.myweatherv3.model.SearchRequest;

import java.util.ArrayList;

public class CityDataOnlyNeed {
    private static final String TAG = "myLogs";
    private Parcel parcel;

    public CityDataOnlyNeed(Parcel parcel, SearchRequest searchRequest) {
        this.parcel = parcel;
        searchCity(searchRequest);
    }

    private void searchCity(SearchRequest searchRequest) {
        float lon = searchRequest.getCoord().getLon();
        float lat = searchRequest.getCoord().getLat();
        Log.e(TAG, "CityCoordinates. Coord: lon - " + lon + " lat: " + lat);
        Log.e(TAG, "requestFlag: 1");
        ArrayList<String> listData = parcel.getDataCites();
        listData.add(searchRequest.getName());
        parcel.setDataCites(listData);
        parcel.setCityName(searchRequest.getName());
        parcel.setLat(lat);
        parcel.setLon(lon);
        parcel.setRequestFlag(1);
    }
}
