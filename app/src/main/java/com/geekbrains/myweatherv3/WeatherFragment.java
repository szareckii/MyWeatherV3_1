package com.geekbrains.myweatherv3;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.geekbrains.myweatherv3.bd.App;
import com.geekbrains.myweatherv3.bd.City;
import com.geekbrains.myweatherv3.bd.WeatherDao;
import com.geekbrains.myweatherv3.bd.WeatherSource;
import com.geekbrains.myweatherv3.model.WeatherRequest;
import com.geekbrains.myweatherv3.weatherdata.RetrofitAdapter;
import com.geekbrains.myweatherv3.weatherdata.WeatherDataOnlyNeed;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private OpenWeather openWeather;
    private WeatherSource weatherSource;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_weather, container, false);
        setRetainInstance(true);
        findViews(layout);

        Log.e(TAG, "WeatherFragment - onCreateView");
        parcel = MainActivity.getParcel();
        parcel.setCurrentFragmentName(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentById(R.id.container)).getTag());
        String cityName = parcel.getCityName();
        cityNameView.setText(cityName);

        findCurrentHour();

        //RETROFIT
        RetrofitAdapter retrofitAdapter = new RetrofitAdapter();
        openWeather = retrofitAdapter.getOpenWeather();

        Log.d(TAG, "WeatherFragment - START RETROFIT");
        requestRetrofit(parcel.getLat(), parcel.getLon());
        Log.d(TAG, "WeatherFragment - END RETROFIT");

        return layout;
    }

    private void requestRetrofit(Float lat, Float lon) {
        String msgError = getString(R.string.check_weather);
        openWeather.loadWeather("minutely", "metric", BuildConfig.WEATHER_API_KEY, lat, lon)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequest> call, @NonNull Response<WeatherRequest> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            new WeatherDataOnlyNeed(parcel,  response);
                            Log.d(TAG, " new WeatherDataOnlyNeed(parcel,  response);");
                            setWeatherData(parcel.getCountHoursBetweenForecasts());
                        } else {
                                showToast(msgError);
                            Log.d(TAG, "response.body() = null");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<WeatherRequest> call, @NonNull Throwable t) {
                            showToast(msgError);
                        Log.d(TAG, "onFailure");
                        Log.d(TAG, "failure " + t);
                    }
                });
    }

    //запись города c историей в БД
    private void addCityWithWeatherToDB(String cityName, float lon, float lat, Date dateTemp, String temper) {
        new Thread(() -> {
            WeatherDao weatherDao = App
                    .getInstance()
                    .getWeatherDao();
            weatherSource = new WeatherSource(weatherDao);

            City city = new City();
            city.cityName = cityName;
            city.lon = lon;
            city.lat = lat;
            weatherSource.addCityWithWeather(city, dateTemp, temper);
        }).start();
    }

    private void setWeatherData(int countHoursBetweenForecasts) {
        Log.d(TAG, "START setWeatherDate");
        textTempCurrent.setText(parcel.getTempCurrent());
        textWindNow.setText(parcel.getWindNow());
        textPressureNow.setText(parcel.getPressureNow());
        textWindDegree.setText(getDegreeWind(parcel.getWindDegree()));

        Picasso.get()
                .load(setIconWeatherURL(parcel.getTypesWeather()))
                .into(imageTypesWeather);

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

        Log.d(TAG, "END setWeatherDate");

        Date currentDate = new Date();
        addCityWithWeatherToDB(parcel.getCityName(), parcel.getLon(), parcel.getLat(), currentDate, parcel.getTempCurrent());
        Log.d(TAG, "END addCityWithWeatherToDB");

    }

    public void showToast(final String toast) {
        Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
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
                        setIconWeatherURL(listHours.get(0).drawableHourImageView),
                        listHours.get(0).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(1).textHour,
                        setIconWeatherURL(listHours.get(1).drawableHourImageView),
                        listHours.get(1).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(2).textHour,
                        setIconWeatherURL(listHours.get(2).drawableHourImageView),
                        listHours.get(2).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(3).textHour,
                        setIconWeatherURL(listHours.get(3).drawableHourImageView),
                        listHours.get(3).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(4).textHour,
                        setIconWeatherURL(listHours.get(4).drawableHourImageView),
                        listHours.get(4).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(5).textHour,
                        setIconWeatherURL(listHours.get(5).drawableHourImageView),
                        listHours.get(5).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(6).textHour,
                        setIconWeatherURL(listHours.get(6).drawableHourImageView),
                        listHours.get(6).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(7).textHour,
                        setIconWeatherURL(listHours.get(7).drawableHourImageView),
                        listHours.get(7).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(8).textHour,
                        setIconWeatherURL(listHours.get(8).drawableHourImageView),
                        listHours.get(8).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(9).textHour,
                        setIconWeatherURL(listHours.get(9).drawableHourImageView),
                        listHours.get(9).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(10).textHour,
                        setIconWeatherURL(listHours.get(10).drawableHourImageView),
                        listHours.get(10).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(11).textHour,
                        setIconWeatherURL(listHours.get(11).drawableHourImageView),
                        listHours.get(11).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(12).textHour,
                        setIconWeatherURL(listHours.get(12).drawableHourImageView),
                        listHours.get(12).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(13).textHour,
                        setIconWeatherURL(listHours.get(13).drawableHourImageView),
                        listHours.get(13).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(14).textHour,
                        setIconWeatherURL(listHours.get(14).drawableHourImageView),
                        listHours.get(14).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(15).textHour,
                        setIconWeatherURL(listHours.get(15).drawableHourImageView),
                        listHours.get(15).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(16).textHour,
                        setIconWeatherURL(listHours.get(16).drawableHourImageView),
                        listHours.get(16).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(17).textHour,
                        setIconWeatherURL(listHours.get(17).drawableHourImageView),
                        listHours.get(17).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(18).textHour,
                        setIconWeatherURL(listHours.get(18).drawableHourImageView),
                        listHours.get(18).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(19).textHour,
                        setIconWeatherURL(listHours.get(19).drawableHourImageView),
                        listHours.get(19).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(20).textHour,
                        setIconWeatherURL(listHours.get(20).drawableHourImageView),
                        listHours.get(20).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(21).textHour,
                        setIconWeatherURL(listHours.get(21).drawableHourImageView),
                        listHours.get(21).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(22).textHour,
                        setIconWeatherURL(listHours.get(22).drawableHourImageView),
                        listHours.get(22).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(23).textHour,
                        setIconWeatherURL(listHours.get(23).drawableHourImageView),
                        listHours.get(23).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(24).textHour,
                        setIconWeatherURL(listHours.get(24).drawableHourImageView),
                        listHours.get(24).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(25).textHour,
                        setIconWeatherURL(listHours.get(25).drawableHourImageView),
                        listHours.get(25).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(26).textHour,
                        setIconWeatherURL(listHours.get(26).drawableHourImageView),
                        listHours.get(26).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(27).textHour,
                        setIconWeatherURL(listHours.get(27).drawableHourImageView),
                        listHours.get(27).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(28).textHour,
                        setIconWeatherURL(listHours.get(28).drawableHourImageView),
                        listHours.get(28).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(29).textHour,
                        setIconWeatherURL(listHours.get(29).drawableHourImageView),
                        listHours.get(29).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(30).textHour,
                        setIconWeatherURL(listHours.get(30).drawableHourImageView),
                        listHours.get(30).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(31).textHour,
                        setIconWeatherURL(listHours.get(31).drawableHourImageView),
                        listHours.get(31).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(32).textHour,
                        setIconWeatherURL(listHours.get(32).drawableHourImageView),
                        listHours.get(32).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(33).textHour,
                        setIconWeatherURL(listHours.get(33).drawableHourImageView),
                        listHours.get(33).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(34).textHour,
                        setIconWeatherURL(listHours.get(34).drawableHourImageView),
                        listHours.get(34).texTempHour),
                new DataClassOfHoursWithDrawable(listHours.get(35).textHour,
                        setIconWeatherURL(listHours.get(35).drawableHourImageView),
                        listHours.get(35).texTempHour)};
    }

    private DataClassOfDaysWithDrawable[] getDataClassOfDays() {
        return new DataClassOfDaysWithDrawable[]{
                new DataClassOfDaysWithDrawable(listDays.get(0).textDay,
                        listDays.get(0).texTemptDay,
                        listDays.get(0).texTemptNight,
                        setIconWeatherURL(listDays.get(0).drawableDay)),
                new DataClassOfDaysWithDrawable(listDays.get(1).textDay,
                        listDays.get(1).texTemptDay,
                        listDays.get(1).texTemptNight,
                        setIconWeatherURL(listDays.get(1).drawableDay)),
                new DataClassOfDaysWithDrawable(listDays.get(2).textDay,
                        listDays.get(2).texTemptDay,
                        listDays.get(2).texTemptNight,
                        setIconWeatherURL(listDays.get(2).drawableDay)),
                new DataClassOfDaysWithDrawable(listDays.get(3).textDay,
                        listDays.get(3).texTemptDay,
                        listDays.get(3).texTemptNight,
                        setIconWeatherURL(listDays.get(3).drawableDay)),
                new DataClassOfDaysWithDrawable(listDays.get(4).textDay,
                        listDays.get(4).texTemptDay,
                        listDays.get(4).texTemptNight,
                        setIconWeatherURL(listDays.get(4).drawableDay)),
                new DataClassOfDaysWithDrawable(listDays.get(5).textDay,
                        listDays.get(5).texTemptDay,
                        listDays.get(5).texTemptNight,
                        setIconWeatherURL(listDays.get(5).drawableDay)),
                new DataClassOfDaysWithDrawable(listDays.get(6).textDay,
                        listDays.get(6).texTemptDay,
                        listDays.get(6).texTemptNight,
                        setIconWeatherURL(listDays.get(6).drawableDay)),
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
    private String setIconWeatherURL(String typeWeather) {
        String typeWeatherURL = "";
        switch (typeWeather) {
            case "01d":
                typeWeatherURL = "http://openweathermap.org/img/wn/01d@2x.png";
                break;
            case "02d":
                typeWeatherURL = "http://openweathermap.org/img/wn/02d@2x.png";
                break;
            case "03d":
                typeWeatherURL = "http://openweathermap.org/img/wn/03d@2x.png";
                break;
            case "04d":
                typeWeatherURL = "http://openweathermap.org/img/wn/04d@2x.png";
                break;
            case "09d":
                typeWeatherURL = "http://openweathermap.org/img/wn/09d@2x.png";
                break;
            case "10d":
                typeWeatherURL = "http://openweathermap.org/img/wn/10d@2x.png";
                break;
            case "11d":
                typeWeatherURL = "http://openweathermap.org/img/wn/11d@2x.png";
                break;
            case "13d":
                typeWeatherURL = "http://openweathermap.org/img/wn/13d@2x.png";
                break;
            case "50d":
                typeWeatherURL = "http://openweathermap.org/img/wn/50d@2x.png";
                break;
            case "01n":
                typeWeatherURL = "http://openweathermap.org/img/wn/01n@2x.png";
                break;
            case "02n":
                typeWeatherURL = "http://openweathermap.org/img/wn/02n@2x.png";
                break;
            case "03n":
                typeWeatherURL = "http://openweathermap.org/img/wn/03n@2x.png";
                break;
            case "04n":
                typeWeatherURL = "http://openweathermap.org/img/wn/04n@2x.png";
                break;
            case "09n":
                typeWeatherURL = "http://openweathermap.org/img/wn/09n@2x.png";
                break;
            case "10n":
                typeWeatherURL = "http://openweathermap.org/img/wn/10n@2x.png";
                break;
            case "11n":
                typeWeatherURL = "http://openweathermap.org/img/wn/11n@2x.png";
                break;
            case "13n":
                typeWeatherURL = "http://openweathermap.org/img/wn/13n@2x.png";
                break;
            case "50n":
                typeWeatherURL = "http://openweathermap.org/img/wn/50n@2x.png";
                break;
        }
        return typeWeatherURL;
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

