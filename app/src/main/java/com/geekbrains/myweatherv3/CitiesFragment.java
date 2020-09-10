package com.geekbrains.myweatherv3;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geekbrains.myweatherv3.bd.App;
import com.geekbrains.myweatherv3.bd.CityWithHistory;
import com.geekbrains.myweatherv3.bd.RecyclerHistoryAdapter;
import com.geekbrains.myweatherv3.model.SearchRequest;
import com.geekbrains.myweatherv3.weatherdata.CityDataOnlyNeed;
import com.geekbrains.myweatherv3.weatherdata.RetrofitAdapter;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Фрагмент выбора города из списка
public class CitiesFragment extends Fragment  implements IRVOnItemClick {
    private static final String TAG = "myLogs";
    private View rootView;
    private RecyclerView recyclerView;
    private ArrayList<String> listData;
    private Parcel parcel;
    private OpenWeather openWeather;
    private List<CityWithHistory> cities;

    // При создании фрагмента укажем его макет
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        findViews();
        setRetainInstance(true);

        parcel = MainActivity.getParcel();
        parcel.setCurrentFragmentName(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentById(R.id.container)).getTag());

        setHistoryByDate();
        Log.d(TAG, "CitiesFragment. onCreateView()");

        RetrofitAdapter retrofitAdapter = new RetrofitAdapter();
        openWeather = retrofitAdapter.getOpenWeather();

        return rootView;
    }

    //Получаем список истории в потоке
    private void setHistoryByDate() {
        Handler handler = new Handler();
        new Thread(() -> {
            List<CityWithHistory> histories = App.getInstance().getWeatherDao().getCityWithHistory();
            handler.post(() -> {
                cities = histories;
                setupRecyclerView();
            });
        }).start();
    }

    // Инициализация полей
    private void findViews() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    //Заполняем recyclerView
    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listData = parcel.getDataCites();
        recyclerView.setLayoutManager(layoutManager);
        RecyclerHistoryAdapter adapter = new RecyclerHistoryAdapter(cities, this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
        Log.d(TAG, "CityRecyclerAdapter()!!!!!!");
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //Метод обработки нажатия по городу из списка
    @Override
    public void onItemClicked(String itemText, final float lon, final float lat) {
        Snackbar.make(requireView(), getString(R.string.choose_city_snackbar) + " " + itemText + "?",  Snackbar.LENGTH_LONG)
                .setAction(R.string.ok_button, v -> {
                    Log.d(TAG, "RecyclerDataAdapter. setOnClickForItem() - " + itemText);
                    boolean visibleWind = parcel.isVisibleWind();
                    boolean visiblePressure = parcel.isVisiblePressure();
                    boolean darkTheme = parcel.isDarkTheme();
                    int countHoursBetweenForecasts = parcel.getCountHoursBetweenForecasts();

                    parcel.setCityName(itemText);
                    parcel.setVisibleWind(visibleWind);
                    parcel.setVisiblePressure(visiblePressure);
                    parcel.setCountHoursBetweenForecasts(countHoursBetweenForecasts);
                    parcel.setDarkTheme(darkTheme);
                    parcel.setDataCites(listData);
                    parcel.setLon(lon);
                    parcel.setLat(lat);

                    requestRetrofit(itemText);

//                    //Заменяем на фрагмент с погодой
                    if (getActivity() != null) {
                        MainActivity ma = (MainActivity) getActivity();
                        ma.setWeatherFragment();
                    }
                }).show();
    }

    // Сохраним текущую позицию (вызывается перед выходом из фрагмента)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //+ Также меняем текущую позицию на Parcel
        outState.putSerializable("CurrentCity", parcel);
        super.onSaveInstanceState(outState);
    }

    private void requestRetrofit(String city) {
        openWeather.loadCityCoord(city, BuildConfig.WEATHER_API_KEY, "ru")
                .enqueue(new Callback<SearchRequest>() {
                    @Override
                    public void onResponse(@NonNull Call<SearchRequest> call, @NonNull Response<SearchRequest> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            new CityDataOnlyNeed(parcel, response);

                        } else {
                            showToast(getString(R.string.check_cityname));
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<SearchRequest> call, @NonNull Throwable t) {
                        showToast(getString(R.string.check_cityname));
                    }
                });
    }

    public void showToast(final String toast) {
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
    }

}

