package com.geekbrains.myweatherv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.geekbrains.myweatherv3.model.SearchRequest;
import com.geekbrains.myweatherv3.weatherdata.CityDataOnlyNeed;
import com.geekbrains.myweatherv3.weatherdata.RetrofitAdapter;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private static final String TAG = "myLogs";
    private DrawerLayout drawer;
    private WeatherFragment weatherFragment;
    private CitiesFragment citiesFragment;
    private SettingsFragment settingsFragment;
    private DeveloperFragment developerFragment;
    private FeedbackFragment feedbackFragment;
    private static Parcel parcel;
    private Pattern checkCityName = Pattern.compile("^[а-яА-ЯёЁa-zA-Z0-9-]+$");
    private final String TAG_WEATHER = "class com.geekbrains.myweatherv3.WeatherFragment";
    private final String TAG_CITIES = "class com.geekbrains.myweatherv3.CitiesFragment";
    private final String TAG_SETTINGS = "class com.geekbrains.myweatherv3.SettingsFragment";
    private final String TAG_DEVELOPER = "class com.geekbrains.myweatherv3.DeveloperFragment";
    private final String TAG_FEEDBACK = "class com.geekbrains.myweatherv3.FeedbackFragment";
    private String cityName;
    private Stack<Fragment> fragmentStack;
    private OpenWeather openWeather;
    private String msgError;
    private SharedPreferences sharePref;
    // имя файла настроек
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_NAME = "currentСity";
    public static final String APP_PREFERENCES_LON = "lon";
    public static final String APP_PREFERENCES_LAT = "lat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean darkTheme;
        if (parcel == null) {
            darkTheme = false;
        } else {
            darkTheme = parcel.isDarkTheme();
        }

        if (darkTheme) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        setContentView(R.layout.activity_main);

        Log.e(TAG, "MainActivity - onCreateView");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        msgError = getString(R.string.check_cityname);

        RetrofitAdapter retrofitAdapter = new RetrofitAdapter();
        openWeather = retrofitAdapter.getOpenWeather();

        weatherFragment = new WeatherFragment();
        citiesFragment = new CitiesFragment();
        settingsFragment = new SettingsFragment();
        developerFragment = new DeveloperFragment();
        feedbackFragment = new FeedbackFragment();

        Log.e(TAG, "AFTER weatherFragment = new WeatherFragment()");

        fragmentStack = new Stack<>();

        if (parcel == null) {
            String[] cities = getResources().getStringArray(R.array.cities);
            ArrayList<String> listData = new ArrayList<>(Arrays.asList(cities));

            /*Создаем стэк вызовов фрагментов для дальнейшего использования в подсветке бокового меню*/
            parcel = new Parcel("Москва", true, true,
                    1, false, listData, 37.62f, 55.75f, TAG_WEATHER/*, fragmentStack*/);
        }

        sharePref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        //получение данных с SharePreference
        if(sharePref.contains(APP_PREFERENCES_NAME)) {
            parcel.setCityName(sharePref.getString(APP_PREFERENCES_NAME, ""));
            parcel.setLon(sharePref.getFloat(APP_PREFERENCES_LON, 0f));
            parcel.setLat(sharePref.getFloat(APP_PREFERENCES_LAT, 0f));
            Log.e(TAG, "SharedPreferences. GET: " + parcel.getCityName());
        }

        setWeatherFragment();

        navigationView.setCheckedItem(R.id.nav_weather);

        if (parcel.getCurrentFragmentName() != null) {
            String tag = parcel.getCurrentFragmentName();
            if(citiesFragment.getClass().toString().equals(tag)){
                setCitiesFragment();
            } else if(settingsFragment.getClass().toString().equals(tag)){
                setSettingsFragment();
            } else if(developerFragment.getClass().toString().equals(tag)){
                setDeveloperFragment();
            } else if(feedbackFragment.getClass().toString().equals(tag)){
                setFeedbackFragment();
            }
        }

        setOnClickForSideMenuItems();
    }

    // Сохраняем настройки
    private void savePreferences(SharedPreferences sharedPref){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(APP_PREFERENCES_NAME, parcel.getCityName());
        editor.putFloat(APP_PREFERENCES_LON, parcel.getLon());
        editor.putFloat(APP_PREFERENCES_LAT, parcel.getLat());
        editor.apply();
        Log.e(TAG, "SharedPreferences. PUT: " + parcel.getCityName());
    }

    /*Метод смены темы светлая-темная*/
    public void ToggleTheme(boolean isChecked){
        if (isChecked) {
            this.getTheme().applyStyle(R.style.AppDarkTheme, true);
        }
        else{
            this.getTheme().applyStyle(R.style.AppTheme, true);
        }
        recreate();
    }
    public static Parcel getParcel() {
        return parcel;
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePreferences(sharePref);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            fragmentStack.pop();
            Log.e(TAG, "getSupportFragmentManager().getBackStackEntryCount(): " + getSupportFragmentManager().getBackStackEntryCount());

            switch (Objects.requireNonNull(fragmentStack.peek().getTag())) {
                case TAG_WEATHER : {
                    navigationView.setCheckedItem(R.id.nav_weather);
                    break;
                }
                case TAG_CITIES : {
                    navigationView.setCheckedItem(R.id.nav_cities);
                    break;
                }
                case TAG_SETTINGS : {
                    navigationView.setCheckedItem(R.id.nav_settings);
                    break;
                }
                case TAG_DEVELOPER : {
                    navigationView.setCheckedItem(R.id.nav_developer);
                    break;
                }
                case TAG_FEEDBACK : {
                    navigationView.setCheckedItem(R.id.nav_feedback);
                    break;
                }
            }

        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        }
        super.onBackPressed();
    }

    @Override
    protected void onRestoreInstanceState(@androidx.annotation.NonNull Bundle saveInstanceState){
        super.onRestoreInstanceState(saveInstanceState);
        Log.d(TAG, "MainAct. onRestoreInstanceState()");
        parcel = (Parcel) saveInstanceState.getSerializable("Parcel");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle saveInstanceState){
        super.onSaveInstanceState(saveInstanceState);
        Log.d(TAG, "MainAct. onSaveInstanceState()");
        saveInstanceState.putSerializable("Parcel", parcel);
    }

    /*Метод нажатия кнопок в баре*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*если нажата кнопка "добавить город"*/
        if (item.getItemId() == R.id.action_add_city) {
            // Создаем билдер и передаем контекст приложения
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            // Вытащим макет диалога
            final View contentView = getLayoutInflater().inflate(R.layout.add_city_dialog, null);
            // в билдере указываем заголовок окна (можно указывать как ресурс, так и строку)
            builder.setTitle(R.string.enter_name)
                    // Установим макет диалога (можно устанавливать любой view)
                    .setView(contentView)
                    .setPositiveButton(R.string.done, (dialogInterface, i) -> {
                        EditText editText = contentView.findViewById(R.id.addCityInput);
                        cityName = editText.getText().toString();

                        if (checkCityName.matcher(cityName).matches()) {    // Проверим на основе регулярных выражений
                            requestRetrofit(cityName);
                        } else {
                            showToast(msgError);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        /*если нажата кнопка "погода яндекса"*/
        } else if (item.getItemId() == R.id.action_yandex) {
            Uri uri = Uri.parse(findYandexWeatherHttp(parcel.getCityName()));
            Intent browser = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(browser);
        /*если нажата кнопка "Климат города"*/
        } else if (item.getItemId() == R.id.action_city_climate) {
            Uri uri = Uri.parse(findWikiWeatherHttp(parcel.getCityName()));
            Intent browser = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(browser);
        /*если нажата кнопка "выход"*/
        } else if (item.getItemId() == R.id.action_exit) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestRetrofit(String city) {
        openWeather.loadCityCoord(city, BuildConfig.WEATHER_API_KEY, "ru")
                .enqueue(new Callback<SearchRequest>() {
                    @Override
                    public void onResponse(@NonNull Call<SearchRequest> call, @NonNull Response<SearchRequest> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            new CityDataOnlyNeed(parcel, response);
                            Log.d(TAG, "new CityDataOnlyNeed(parcel, response);");
                            setWeatherFragment();
                        } else {
                            showToast(getString(R.string.check_cityname));
                            Log.d(TAG, "ACTIVITY response.body() = null");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<SearchRequest> call, @NonNull Throwable t) {
                        showToast(getString(R.string.check_cityname));
                        Log.d(TAG, "failure " + t);
                        Log.d(TAG, "ACTIVITY onFailure");
                    }
                });
    }

    public void showToast(final String toast) {
        Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
    }

    /*Метод составления http с погодой от яндекса к выбранному городу*/
    private String findYandexWeatherHttp(String cityName) {
        String yandexWeatherHttp = "https://yandex.ru/pogoda/";
        switch (cityName) {
            case "Moscow":
            case "Москва":
                yandexWeatherHttp += "moscow";
                break;
            case "London":
            case "Лондон":
                yandexWeatherHttp += "10393";
                break;
            case "New York":
            case "Нью-Йорк":
                yandexWeatherHttp += "202";
                break;
            case "Beijing":
            case "Пекин":
                yandexWeatherHttp += "10590";
                break;
            case "Paris":
            case "Париж":
                yandexWeatherHttp += "10502";
                break;
            default:
                yandexWeatherHttp += cityName;
                break;
        }
        return yandexWeatherHttp;
    }

    /*Метод составления http с информацией по климату от Вики выбранного города*/
    private String findWikiWeatherHttp(String cityName) {
        String wikiWeatherHttp = "https://ru.wikipedia.org/wiki/";
        switch (cityName) {
            case "Moscow":
            case "Москва":
                wikiWeatherHttp += "Климат_Москвы";
                break;
            case "London":
            case "Лондон":
                wikiWeatherHttp += "Климат_Лондона";
                break;
            case "New York":
            case "Нью-Йорк":
                wikiWeatherHttp += "Нью-Йорк#Климат";
                break;
            case "Beijing":
            case "Пекин":
                wikiWeatherHttp += "Пекин#Климат";
                break;
            case "Paris":
            case "Париж":
                wikiWeatherHttp += "Париж#Климат";
                break;
            default:
                wikiWeatherHttp += cityName;
                break;
        }
        return wikiWeatherHttp;
    }

    /*Метод обработки нажатия по боковому меню*/
    private void setOnClickForSideMenuItems() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_weather: {
                    setWeatherFragment();
                    drawer.close();
                    break;
                }
                case R.id.nav_cities: {
                    setCitiesFragment();
                    drawer.close();
                    break;
                }
                case R.id.nav_settings: {
                    setSettingsFragment();
                    drawer.close();
                    break;
                }
                case R.id.nav_developer: {
                    setDeveloperFragment();
                    drawer.close();
                    break;
                }
                case R.id.nav_feedback: {
                    setFeedbackFragment();
                    drawer.close();
                    break;
                }
            }
            return true;
        });
    }

    public void setWeatherFragment() {
        Log.e(TAG, "setWeatherFragment");
        weatherFragment = new WeatherFragment();
        setFragment(weatherFragment, TAG_WEATHER);
        fragmentStack.push(weatherFragment);
        navigationView.setCheckedItem(R.id.nav_weather);
    }

    private void setCitiesFragment() {
        Log.e(TAG, "setCitiesFragment");
        citiesFragment = new CitiesFragment();
        setFragment(citiesFragment, TAG_CITIES);
        fragmentStack.push(citiesFragment);
    }

    private void setSettingsFragment() {
        Log.e(TAG, "setSettingsFragment");
        settingsFragment = new SettingsFragment();
        setFragment(settingsFragment, TAG_SETTINGS);
        fragmentStack.push(settingsFragment);
    }

    private void setDeveloperFragment() {
        Log.e(TAG, "setDeveloperFragment");
        developerFragment = new DeveloperFragment();
        setFragment(developerFragment, TAG_DEVELOPER);
        fragmentStack.push(developerFragment);
    }

    private void setFeedbackFragment() {
        Log.e(TAG, "setFeedbackFragment");
        feedbackFragment = new FeedbackFragment();
        setFragment(feedbackFragment, TAG_FEEDBACK);
        fragmentStack.push(feedbackFragment);
    }

    private void setFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}








