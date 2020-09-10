package com.geekbrains.myweatherv3;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.Objects;

// Фрагмент выбора города из списка
public class CitiesFragment extends Fragment  implements IRVOnItemClick {
    private static final String TAG = "myLogs";
    private View rootView;
    private RecyclerView recyclerView;
    private ArrayList<String> listData;
    private float lon = 0;
    private float lat = 0;
    private Parcel parcel;

    // При создании фрагмента укажем его макет
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list, container, false);
        findViews();
        setRetainInstance(true);

        parcel = MainActivity.getParcel();
        parcel.setCurrentFragmentName(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentById(R.id.container)).getTag());

        setupRecyclerView();
        Log.d(TAG, "CitiesFragment. onCreateView()");

        return rootView;
    }

    // Инициализация полей
    private void findViews() {
        recyclerView = rootView.findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);
        listData = parcel.getData();
        RecyclerDataAdapterForCity adapter = new RecyclerDataAdapterForCity(listData, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    //Метод обработки нажатия по городу из списка
    @Override
    public void onItemClicked(String itemText) {
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
                    parcel.setData(listData);
                    parcel.setLon(lon);
                    parcel.setLat(lat);

                    //Заменяем на фрагмент с погодой
                    Fragment newFragment = new WeatherFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, newFragment);
                    transaction.commit();

                }).show();
    }

    // Сохраним текущую позицию (вызывается перед выходом из фрагмента)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //+ Также меняем текущую позицию на Parcel
        outState.putSerializable("CurrentCity", parcel);
        super.onSaveInstanceState(outState);
    }
}

