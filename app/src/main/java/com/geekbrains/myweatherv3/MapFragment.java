package com.geekbrains.myweatherv3;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MapFragment extends Fragment  implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Parcel parcel;
    private static final String TAG = "myLogs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_map, container, false);
        setRetainInstance(true);

        parcel = MainActivity.getParcel();
        parcel.setCurrentFragmentName(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentById(R.id.container)).getTag());

        SupportMapFragment mMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        assert mMapFragment != null;
        mMapFragment.getMapAsync(this);

        return layout;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng currentPosition = new LatLng(parcel.getLat(), parcel.getLon());
        Marker currentMarker = mMap.addMarker(new MarkerOptions().position(currentPosition).title("Текущая позиция"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(parcel.getLat(), parcel.getLon()))
                .zoom(7)
                .build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);

        mMap.addMarker(new MarkerOptions().position(currentPosition).title("Marker in current position"));
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        initMapListener();
    }

    //Метод получения координат по точке нажатия по карте
    private void initMapListener() {
        mMap.setOnMapClickListener(latLng -> {
            Log.d(TAG, "onMapClick: " + latLng.latitude + "," + latLng.longitude);

            parcel.setLat((float) latLng.latitude);
            parcel.setLon((float) latLng.longitude);

            //Заменяем на фрагмент с погодой
            if (getActivity() != null) {
                MainActivity ma = (MainActivity) getActivity();
                parcel.setCityName(ma.getAddressByLoc((float) latLng.latitude, (float) latLng.longitude));
                ma.setWeatherFragment();
            }
        });
    }
}
