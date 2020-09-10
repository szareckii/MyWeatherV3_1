package com.geekbrains.myweatherv3;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import com.geekbrains.myweatherv3.weatherdata.WeatherData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;

public class WeatherFragment extends Fragment {
    private TextView textTempCurrent;
    private ImageView imageTypesWeather;
    private TextView textWindNow;
    private TextView textPressureNow;
    private TextView textWindDegree;
    private static final String TAG = "myLogs";
    private Parcel parcel;
    private TextView textUnitWind;
    private TextView textUnitPressureNow;
    private TextView cityNameView;
    private ArrayList<DataClassOfHours> listHours;
    private ArrayList<DataClassOfDays> listDays;
    private RecyclerView daysRecyclerView;
    private RecyclerView hoursRecyclerView;
    int a01n, a02n, a03n, a04n, a09n, a10n, a11n, a13n, a50n;
    int a01d, a02d, a03d, a04d, a09d, a10d, a11d, a13d, a50d;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_weather, container, false);
        setRetainInstance(true);
        findViews(layout);

        Log.e(TAG, "setWeatherFragment - onCreateView");
        parcel = MainActivity.getParcel();
        parcel.setCurrentFragmentName(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentById(R.id.container)).getTag());
        String cityName = parcel.getCityName();
        cityNameView.setText(cityName);

        int countHoursBetweenForecasts = parcel.getCountHoursBetweenForecasts();

        findCurrentHour();

        final Handler handler = new Handler(); // Запоминаем основной поток
        new Thread(() -> {
            try {
                WeatherData weatherData = new WeatherData(parcel);
                weatherData.setWhether();
                while (true) {
                    // если город найден то прекращаем ждать завершения работы
                    if (parcel.getRequestWeatherFlag() == 1) {
                        Log.e(TAG, "getRequestWeatherFlag() == 1");
                        parcel.setRequestWeatherFlag(0);
                        handler.post(() -> setWeatherData(countHoursBetweenForecasts));
                        break;
                        // если город не найден то выводим сообщение
                    } else if (parcel.getRequestWeatherFlag() == -1) {
                        Log.e(TAG, "getRequestWeatherFlag() == -1");
                        parcel.setRequestWeatherFlag(0);
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Fail connection", e);
                e.printStackTrace();
            }
        }).start();
        return layout;
    }

    private void setWeatherData(int countHoursBetweenForecasts) {
        Log.d(TAG, "setWeatherDate");
        textTempCurrent.setText(parcel.getTempCurrent());
        textWindNow.setText(parcel.getWindNow());
        textPressureNow.setText(parcel.getPressureNow());
        textWindDegree.setText(getDegreeWind(parcel.getWindDegree()));
        imageTypesWeather.setImageResource(setIconWeather(parcel.getTypesWeather(), "Current"));
        textUnitWind.setText(R.string.textUnitWind);
        textUnitPressureNow.setText(R.string.textUnitPressureNow);

        setVisiblePressure(parcel.isVisiblePressure());
        setVisibleWindy(parcel.isVisibleWind());

        /*Температура на 36 часов*/
        listHours = new ArrayList<>(36);
        listHours.clear();
        listHours.addAll(Arrays.asList(parcel.getDataHours()));
        ArrayList<DataClassOfHoursWithDrawable> listHoursWithDrawable = new ArrayList<>(36);
        DataClassOfHoursWithDrawable[] dataHours1 = getDataClassOfHours();
        listHoursWithDrawable.clear();

        for (int i = 0; i < 12; i++) {
            listHoursWithDrawable.add(dataHours1[i * countHoursBetweenForecasts]);
        }

        setupRecyclerViewHours(listHoursWithDrawable);

//        /*Температура на 7 дней*/
        listDays = new ArrayList<>(7);
        listDays.clear();
        listDays.addAll(Arrays.asList(parcel.getDataDays()));
        ArrayList<DataClassOfDaysWithDrawable> listDaysWithDrawable = new ArrayList<>(7);
        DataClassOfDaysWithDrawable[] dataDays1 = getDataClassOfDays();
        listDaysWithDrawable.clear();
        listDaysWithDrawable.addAll(Arrays.asList(dataDays1));
        setupRecyclerViewDays(listDaysWithDrawable);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e(TAG, "setWeatherFragment! onViewCreated!");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "setWeatherFragment! onActivityCreated!");
    }

    /*Метод заполнения RecyclerView по часам*/
    private void setupRecyclerViewHours(ArrayList<DataClassOfHoursWithDrawable> list) {
        hoursRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        RecyclerDataAdapterForHours hoursAdapter = new RecyclerDataAdapterForHours(list);
        hoursRecyclerView.setLayoutManager(layoutManager);
        hoursRecyclerView.setAdapter(hoursAdapter);
    }

    /*Метод заполнения RecyclerView по дням*/
    private void setupRecyclerViewDays(ArrayList<DataClassOfDaysWithDrawable> list) {
        //если ориентация горизонтальная, то переопределяем layoutManager для выключения скрола у
        // RecyclerView

        if (this.requireContext().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(getActivity());
            layoutManager.setScrollEnabled(false);
            daysRecyclerView.setLayoutManager(layoutManager);
            daysRecyclerView.addItemDecoration(new DividerItemDecoration(daysRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL));

        } else {
            LinearLayoutManager layoutManager;
            layoutManager = new LinearLayoutManager(getActivity());
            daysRecyclerView.setLayoutManager(layoutManager);
            daysRecyclerView.addItemDecoration(new DividerItemDecoration(daysRecyclerView.getContext(),
                    DividerItemDecoration.VERTICAL));
        }

        daysRecyclerView.setHasFixedSize(true);
        RecyclerDataAdapterForDays daysAdapter = new RecyclerDataAdapterForDays(list);
        daysRecyclerView.setAdapter(daysAdapter);
    }

    /*Метод скрытия/отображения из активити view относящихся к давлению*/
    private void setVisiblePressure(boolean pressureVisible) {
        if (!pressureVisible) {
            textPressureNow.setVisibility(View.GONE);
            textUnitPressureNow.setVisibility(View.GONE);
        }
        else {
            textPressureNow.setVisibility(View.VISIBLE);
            textUnitPressureNow.setVisibility(View.VISIBLE);
        }
    }

    /*Метод скрытия/отображения из активити view относящихся к ветру*/
    private void setVisibleWindy(boolean windyVisible) {
        if (!windyVisible) {
            textWindNow.setVisibility(View.GONE);
            textUnitWind.setVisibility(View.GONE);
            textWindDegree.setVisibility(View.GONE);
        }
        else {
            textWindNow.setVisibility(View.VISIBLE);
            textUnitWind.setVisibility(View.VISIBLE);
            textWindDegree.setVisibility(View.VISIBLE);
        }
    }

    /*Метод определения текущего дня и часа */
    @SuppressLint("SimpleDateFormat")
    private void findCurrentHour() {
        Calendar cPlusOneHour = Calendar.getInstance();
        cPlusOneHour.set(Calendar.MINUTE, 0);
        cPlusOneHour.add(Calendar.HOUR, 1);

        Calendar cPlusTwoHours = Calendar.getInstance();
        cPlusTwoHours.set(Calendar.MINUTE, 0);
        cPlusTwoHours.add(Calendar.HOUR, 2) ;

        Calendar cPlusThreeHours = Calendar.getInstance();
        cPlusThreeHours.set(Calendar.MINUTE, 0);
        cPlusThreeHours.add(Calendar.HOUR, 3);

        Calendar cPlusFourHours = Calendar.getInstance();
        cPlusFourHours.set(Calendar.MINUTE, 0);
        cPlusFourHours.add(Calendar.HOUR, 4);

        Calendar cPlusFiveHours = Calendar.getInstance();
        cPlusFiveHours.set(Calendar.MINUTE, 0);
        cPlusFiveHours.add(Calendar.HOUR, 5);

        Calendar cPlusSixHours = Calendar.getInstance();
        cPlusSixHours.set(Calendar.MINUTE, 0);
        cPlusSixHours.add(Calendar.HOUR, 6);

        Calendar cPlusSevenHours = Calendar.getInstance();
        cPlusSevenHours.set(Calendar.MINUTE, 0);
        cPlusSevenHours.add(Calendar.HOUR, 7);

        Calendar cPlusEightHours = Calendar.getInstance();
        cPlusEightHours.set(Calendar.MINUTE, 0);
        cPlusEightHours.add(Calendar.HOUR, 8);

        Calendar cPlusNineHours = Calendar.getInstance();
        cPlusNineHours.set(Calendar.MINUTE, 0);
        cPlusNineHours.add(Calendar.HOUR, 9);

        Calendar cPlusTenHours = Calendar.getInstance();
        cPlusTenHours.set(Calendar.MINUTE, 0);
        cPlusTenHours.add(Calendar.HOUR, 10);

        Calendar cPlusElevenHours = Calendar.getInstance();
        cPlusElevenHours.set(Calendar.MINUTE, 0);
        cPlusElevenHours.add(Calendar.HOUR, 11);

        Calendar cPlusTwelveHours = Calendar.getInstance();
        cPlusTwelveHours.set(Calendar.MINUTE, 0);
        cPlusTwelveHours.add(Calendar.HOUR, 12);

        Calendar cPlusThirteenHours = Calendar.getInstance();
        cPlusThirteenHours.set(Calendar.MINUTE, 0);
        cPlusThirteenHours.add(Calendar.HOUR, 13);

        Calendar cPlusFourteenHours = Calendar.getInstance();
        cPlusFourteenHours.set(Calendar.MINUTE, 0);
        cPlusFourteenHours.add(Calendar.HOUR, 14) ;

        Calendar cPlusFifteenHours = Calendar.getInstance();
        cPlusFifteenHours.set(Calendar.MINUTE, 0);
        cPlusFifteenHours.add(Calendar.HOUR, 15);

        Calendar cPlusSixteenHours = Calendar.getInstance();
        cPlusSixteenHours.set(Calendar.MINUTE, 0);
        cPlusSixteenHours.add(Calendar.HOUR, 16);

        Calendar cPlusSeventeenHours = Calendar.getInstance();
        cPlusSeventeenHours.set(Calendar.MINUTE, 0);
        cPlusSeventeenHours.add(Calendar.HOUR, 17);

        Calendar cPlusEighteenHours = Calendar.getInstance();
        cPlusEighteenHours.set(Calendar.MINUTE, 0);
        cPlusEighteenHours.add(Calendar.HOUR, 18);

        Calendar cPlusNineteenHours = Calendar.getInstance();
        cPlusNineteenHours.set(Calendar.MINUTE, 0);
        cPlusNineteenHours.add(Calendar.HOUR, 19);

        Calendar cPlusTwentyHours = Calendar.getInstance();
        cPlusTwentyHours.set(Calendar.MINUTE, 0);
        cPlusTwentyHours.add(Calendar.HOUR, 20);

        Calendar cPlusTwentyOneHours = Calendar.getInstance();
        cPlusTwentyOneHours.set(Calendar.MINUTE, 0);
        cPlusTwentyOneHours.add(Calendar.HOUR, 21);

        Calendar cPlusTwentyTwoHours = Calendar.getInstance();
        cPlusTwentyTwoHours.set(Calendar.MINUTE, 0);
        cPlusTwentyTwoHours.add(Calendar.HOUR, 22);

        Calendar cPlusTwentyThreeHours = Calendar.getInstance();
        cPlusTwentyThreeHours.set(Calendar.MINUTE, 0);
        cPlusTwentyThreeHours.add(Calendar.HOUR, 23);

        Calendar cPlusTwentyFourHours = Calendar.getInstance();
        cPlusTwentyFourHours.set(Calendar.MINUTE, 0);
        cPlusTwentyFourHours.add(Calendar.HOUR, 24);

        Calendar cPlusTwentyFiveHours = Calendar.getInstance();
        cPlusTwentyFiveHours.set(Calendar.MINUTE, 0);
        cPlusTwentyFiveHours.add(Calendar.HOUR, 25);

        Calendar cPlusTwentySixHours = Calendar.getInstance();
        cPlusTwentySixHours.set(Calendar.MINUTE, 0);
        cPlusTwentySixHours.add(Calendar.HOUR, 26) ;

        Calendar cPlusTwentySevenHours = Calendar.getInstance();
        cPlusTwentySevenHours.set(Calendar.MINUTE, 0);
        cPlusTwentySevenHours.add(Calendar.HOUR, 27);

        Calendar cPlusTwentyEightHours = Calendar.getInstance();
        cPlusTwentyEightHours.set(Calendar.MINUTE, 0);
        cPlusTwentyEightHours.add(Calendar.HOUR, 28);

        Calendar cPlusTwentyNineHours = Calendar.getInstance();
        cPlusTwentyNineHours.set(Calendar.MINUTE, 0);
        cPlusTwentyNineHours.add(Calendar.HOUR, 29);

        Calendar cPlusThirtyHours = Calendar.getInstance();
        cPlusThirtyHours.set(Calendar.MINUTE, 0);
        cPlusThirtyHours.add(Calendar.HOUR, 30);

        Calendar cPlusThirtyOneHours = Calendar.getInstance();
        cPlusThirtyOneHours.set(Calendar.MINUTE, 0);
        cPlusThirtyOneHours.add(Calendar.HOUR, 31);

        Calendar cPlusThirtyTwoHours = Calendar.getInstance();
        cPlusThirtyTwoHours.set(Calendar.MINUTE, 0);
        cPlusThirtyTwoHours.add(Calendar.HOUR, 32);

        Calendar cPlusThirtyThreeHours = Calendar.getInstance();
        cPlusThirtyThreeHours.set(Calendar.MINUTE, 0);
        cPlusThirtyThreeHours.add(Calendar.HOUR, 33);

        Calendar cPlusThirtyFourHours = Calendar.getInstance();
        cPlusThirtyFourHours.set(Calendar.MINUTE, 0);
        cPlusThirtyFourHours.add(Calendar.HOUR, 34);

        Calendar cPlusThirtyFiveHours = Calendar.getInstance();
        cPlusThirtyFiveHours.set(Calendar.MINUTE, 0);
        cPlusThirtyFiveHours.add(Calendar.HOUR, 35);

        Calendar cPlusThirtySixHours = Calendar.getInstance();
        cPlusThirtySixHours.set(Calendar.MINUTE, 0);
        cPlusThirtySixHours.add(Calendar.HOUR, 36);

//        DateFormat dfHour = new SimpleDateFormat("HH:mm");

        Calendar cDayPlusOne = Calendar.getInstance();
        cDayPlusOne.add(Calendar.DAY_OF_MONTH, 1);
        Calendar cDayPlusTwo = Calendar.getInstance();
        cDayPlusTwo.add(Calendar.DAY_OF_MONTH, 2);
        Calendar cDayPlusThree = Calendar.getInstance();
        cDayPlusThree.add(Calendar.DAY_OF_MONTH, 3);
        Calendar cDayPlusFour = Calendar.getInstance();
        cDayPlusFour.add(Calendar.DAY_OF_MONTH, 4);
        Calendar cDayPlusFive = Calendar.getInstance();
        cDayPlusFive.add(Calendar.DAY_OF_MONTH, 5);
        Calendar cDayPlusSix = Calendar.getInstance();
        cDayPlusSix.add(Calendar.DAY_OF_MONTH, 6);
        Calendar cDayPlusSeven = Calendar.getInstance();
        cDayPlusSeven.add(Calendar.DAY_OF_MONTH, 7);

//        DateFormat df = new SimpleDateFormat("dd/MM");
    }

    private DataClassOfHoursWithDrawable[] getDataClassOfHours() {
    /*почасова погода на 36, т.к. 12 почасовых прогнозо с возможностью выбора между прогнозами
    1, 2 или 3 часа, т.е. 12*3*/

        return new DataClassOfHoursWithDrawable[]{
                new DataClassOfHoursWithDrawable(listHours.get(0).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(0).drawableHourImageView, "Day")),
                        listHours.get(0).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(1).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(1).drawableHourImageView, "Day")),
                        listHours.get(1).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(2).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(2).drawableHourImageView, "Day")),
                        listHours.get(2).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(3).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(3).drawableHourImageView, "Day")),
                        listHours.get(3).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(4).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(4).drawableHourImageView, "Day")),
                        listHours.get(4).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(5).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(5).drawableHourImageView, "Day")),
                        listHours.get(5).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(6).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(6).drawableHourImageView, "Day")),
                        listHours.get(6).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(7).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(7).drawableHourImageView, "Day")),
                        listHours.get(7).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(8).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(8).drawableHourImageView, "Day")),
                        listHours.get(8).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(9).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(9).drawableHourImageView, "Day")),
                        listHours.get(9).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(10).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(10).drawableHourImageView, "Day")),
                        listHours.get(10).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(11).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(11).drawableHourImageView, "Day")),
                        listHours.get(11).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(12).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(12).drawableHourImageView, "Day")),
                        listHours.get(12).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(13).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(13).drawableHourImageView, "Day")),
                        listHours.get(13).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(14).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(14).drawableHourImageView, "Day")),
                        listHours.get(14).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(15).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(15).drawableHourImageView, "Day")),
                        listHours.get(15).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(16).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(16).drawableHourImageView, "Day")),
                        listHours.get(16).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(17).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(17).drawableHourImageView, "Day")),
                        listHours.get(17).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(18).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(18).drawableHourImageView, "Day")),
                        listHours.get(18).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(19).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(19).drawableHourImageView, "Day")),
                        listHours.get(19).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(20).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(20).drawableHourImageView, "Day")),
                        listHours.get(20).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(21).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(21).drawableHourImageView, "Day")),
                        listHours.get(21).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(22).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(22).drawableHourImageView, "Day")),
                        listHours.get(22).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(23).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(23).drawableHourImageView, "Day")),
                        listHours.get(23).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(24).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(24).drawableHourImageView, "Day")),
                        listHours.get(24).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(25).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(25).drawableHourImageView, "Day")),
                        listHours.get(25).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(26).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(26).drawableHourImageView, "Day")),
                        listHours.get(26).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(27).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(27).drawableHourImageView, "Day")),
                        listHours.get(27).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(28).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(28).drawableHourImageView, "Day")),
                        listHours.get(28).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(29).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(29).drawableHourImageView, "Day")),
                        listHours.get(29).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(30).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(30).drawableHourImageView, "Day")),
                        listHours.get(30).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(31).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(31).drawableHourImageView, "Day")),
                        listHours.get(31).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(32).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(32).drawableHourImageView, "Day")),
                        listHours.get(32).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(33).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(33).drawableHourImageView, "Day")),
                        listHours.get(33).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(34).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(34).drawableHourImageView, "Day")),
                        listHours.get(34).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(35).textHour,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listHours.get(35).drawableHourImageView, "Day")),
                        listHours.get(35).texTempHour)};
    }

    private DataClassOfDaysWithDrawable[] getDataClassOfDays() {
        /*заполения массива погоды на 7 дней*/
        return new DataClassOfDaysWithDrawable[]{
                new DataClassOfDaysWithDrawable(listDays.get(0).textDay,
                        listDays.get(0).texTemptDay,
                        listDays.get(0).texTemptNight,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listDays.get(0).drawableDay, "Day"))),
                new DataClassOfDaysWithDrawable(listDays.get(1).textDay,
                        listDays.get(1).texTemptDay,
                        listDays.get(1).texTemptNight,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listDays.get(1).drawableDay, "Day"))),
                new DataClassOfDaysWithDrawable(listDays.get(2).textDay,
                        listDays.get(2).texTemptDay,
                        listDays.get(2).texTemptNight,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listDays.get(2).drawableDay, "Day"))),
                new DataClassOfDaysWithDrawable(listDays.get(3).textDay,
                        listDays.get(3).texTemptDay,
                        listDays.get(3).texTemptNight,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listDays.get(3).drawableDay, "Day"))),
                new DataClassOfDaysWithDrawable(listDays.get(4).textDay,
                        listDays.get(4).texTemptDay,
                        listDays.get(4).texTemptNight,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listDays.get(4).drawableDay, "Day"))),
                new DataClassOfDaysWithDrawable(listDays.get(5).textDay,
                        listDays.get(5).texTemptDay,
                        listDays.get(5).texTemptNight,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listDays.get(5).drawableDay, "Day"))),
                new DataClassOfDaysWithDrawable(listDays.get(6).textDay,
                        listDays.get(6).texTemptDay,
                        listDays.get(6).texTemptNight,
                        ContextCompat.getDrawable(requireActivity(), setIconWeather(listDays.get(6).drawableDay, "Day")))
                        };
    }

    /* Метод вперевода направления ветра из градусов в стороны света*/
    private String getDegreeWind(int degree) {
        String degreeStr = getResources().getString(R.string.textUnitWindN);

        if ((degree >= 337 && degree <= 360) || (degree >= 0 && degree <= 23)) {
            degreeStr = getResources().getString(R.string.textUnitWindN);
        } else if (degree > 23 && degree < 67) {
            degreeStr = getResources().getString(R.string.textUnitWindNE);
        } else if (degree >= 67 && degree <= 113) {
            degreeStr = getResources().getString(R.string.textUnitWindE);
        } else if (degree > 113 && degree < 157) {
            degreeStr = getResources().getString(R.string.textUnitWindSE);
        } else if (degree >= 157 && degree <= 203) {
            degreeStr = getResources().getString(R.string.textUnitWindS);
        } else if (degree > 203 && degree < 248) {
            degreeStr = getResources().getString(R.string.textUnitWindSW);
        } else if (degree >= 248 && degree <= 293) {
            degreeStr = getResources().getString(R.string.textUnitWindW);
        } else if (degree > 293 && degree < 337) {
            degreeStr = getResources().getString(R.string.textUnitWindNW);
        }
        return degreeStr;
    }

    /*Метод поиска икноки погоды по информации из json*/
    private int setIconWeather(String typeWeather1, String time) {
        int typeWeather = 0;
        if (time.equals("Current")) {
            switch (typeWeather1) {
                case "01d":
                    typeWeather = a01d;
                    break;
                case "02d":
                    typeWeather = a02d;
                    break;
                case "03d":
                    typeWeather = a03d;
                    break;
                case "04d":
                    typeWeather = a04d;
                    break;
                case "09d":
                    typeWeather = a09d;
                    break;
                case "10d":
                    typeWeather = a10d;
                    break;
                case "11d":
                    typeWeather = a11d;
                    break;
                case "13d":
                    typeWeather = a13d;
                    break;
                case "50d":
                    typeWeather = a50d;
                    break;
                case "01n":
                    typeWeather = a01n;
                    break;
                case "02n":
                    typeWeather = a02n;
                    break;
                case "03n":
                    typeWeather = a03n;
                    break;
                case "04n":
                    typeWeather = a04n;
                    break;
                case "09n":
                    typeWeather = a09n;
                    break;
                case "10n":
                    typeWeather = a10n;
                    break;
                case "11n":
                    typeWeather = a11n;
                    break;
                case "13n":
                    typeWeather = a13n;
                    break;
                case "50n":
                    typeWeather = a50n;
                    break;
            }
        } else if (time.equals("Day")) {
            switch (typeWeather1) {
                case "01d":
                    typeWeather = a01d;
                    break;
                case "02d":
                    typeWeather = a02d;
                    break;
                case "03d":
                    typeWeather = a03d;
                    break;
                case "04d":
                    typeWeather = a04d;
                    break;
                case "09d":
                    typeWeather = a09d;
                    break;
                case "10d":
                    typeWeather = a10d;
                    break;
                case "11d":
                    typeWeather = a11d;
                    break;
                case "13d":
                    typeWeather = a13d;
                    break;
                case "50d":
                    typeWeather = a50d;
                    break;
                case "01n":
                    typeWeather = a01n;
                    break;
                case "02n":
                    typeWeather = a02n;
                    break;
                case "03n":
                    typeWeather = a03n;
                    break;
                case "04n":
                    typeWeather = a04n;
                    break;
                case "09n":
                    typeWeather = a09n;
                    break;
                case "10n":
                    typeWeather = a10n;
                    break;
                case "11n":
                    typeWeather = a11n;
                    break;
                case "13n":
                    typeWeather = a13n;
                    break;
                case "50n":
                    typeWeather = a50n;
                    break;
            }
        } else {
            switch (typeWeather1) {
                case "01d":
                    typeWeather = a01d;
                    break;
                case "02d":
                    typeWeather = a02d;
                    break;
                case "03d":
                    typeWeather = a03d;
                    break;
                case "04d":
                    typeWeather = a04d;
                    break;
                case "09d":
                    typeWeather = a09d;
                    break;
                case "10d":
                    typeWeather = a10d;
                    break;
                case "11d":
                    typeWeather = a11d;
                    break;
                case "13d":
                    typeWeather = a13d;
                    break;
                case "50d":
                    typeWeather = a50d;
                    break;
                case "01n":
                    typeWeather = a01n;
                    break;
                case "02n":
                    typeWeather = a02n;
                    break;
                case "03n":
                    typeWeather = a03n;
                    break;
                case "04n":
                    typeWeather = a04n;
                    break;
                case "09n":
                    typeWeather = a09n;
                    break;
                case "10n":
                    typeWeather = a10n;
                    break;
                case "11n":
                    typeWeather = a11n;
                    break;
                case "13n":
                    typeWeather = a13n;
                    break;
                case "50n":
                    typeWeather = a50n;
                    break;
            }
        }
        return typeWeather;
    }

    /*Метод инициализации*/
    private void findViews(View layout) {
        cityNameView = layout.findViewById(R.id.textCity);
        textTempCurrent = layout.findViewById(R.id.textTempCurrent);
        imageTypesWeather = layout.findViewById(R.id.imageTypsWeather);
        textWindNow = layout.findViewById(R.id.textWindNow);
        textPressureNow = layout.findViewById(R.id.textPressureNow);
        textUnitWind = layout.findViewById(R.id.textUnitWind);
        textUnitPressureNow = layout.findViewById(R.id.textUnitPressureNow);
        textWindDegree = layout.findViewById(R.id.textWindDegree);
        daysRecyclerView = layout.findViewById(R.id.daysRecyclerView);
        hoursRecyclerView = layout.findViewById(R.id.hoursRecyclerView);

        a01d = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a01d), "drawable", requireActivity().getPackageName());
        a02d = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a02d), "drawable", requireActivity().getPackageName());
        a03d = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a03d), "drawable", requireActivity().getPackageName());
        a04d = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a04d), "drawable", requireActivity().getPackageName());
        a09d = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a09d), "drawable", requireActivity().getPackageName());
        a10d = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a10d), "drawable", requireActivity().getPackageName());
        a11d = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a11d), "drawable", requireActivity().getPackageName());
        a13d = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a13d), "drawable", requireActivity().getPackageName());
        a50d = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a50d), "drawable", requireActivity().getPackageName());
        a01n = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a01n), "drawable", requireActivity().getPackageName());
        a02n = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a02n), "drawable", requireActivity().getPackageName());
        a03n = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a03n), "drawable", requireActivity().getPackageName());
        a04n = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a04n), "drawable", requireActivity().getPackageName());
        a09n = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a09n), "drawable", requireActivity().getPackageName());
        a10n = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a10n), "drawable", requireActivity().getPackageName());
        a11n = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a11n), "drawable", requireActivity().getPackageName());
        a13n = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a13n), "drawable", requireActivity().getPackageName());
        a50n = requireActivity().getResources().getIdentifier(
                String.valueOf(R.drawable.a50n), "drawable", requireActivity().getPackageName());

        setClearTextView();
    }

    /*Метод очистки TextView*/
    private void setClearTextView() {
        textTempCurrent.setText("");
        textWindNow.setText("");
        textUnitWind.setText("");
        textWindDegree.setText("");
        textPressureNow.setText("");
        textUnitPressureNow.setText("");
        imageTypesWeather.setImageResource(R.drawable.close);
    }

}

