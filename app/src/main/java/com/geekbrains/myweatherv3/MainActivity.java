package com.geekbrains.myweatherv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private static final String TAG = "myLogs";
    private DrawerLayout drawer;
    private WeatherFragment weatherFragment;
    private SettingsFragment settingsFragment;
    private static Parcel parcel;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        weatherFragment = new WeatherFragment();
        CitiesFragment citiesFragment = new CitiesFragment();
        settingsFragment = new SettingsFragment();
        DeveloperFragment developerFragment = new DeveloperFragment();
        FeedbackFragment feedbackFragment = new FeedbackFragment();


        if (parcel == null) {
            String[] cities = getResources().getStringArray(R.array.cities);
            ArrayList<String> listData = new ArrayList<>(Arrays.asList(cities));
            parcel = new Parcel("Москва", true, true,
                    1, false, listData, 0, 0,
                    "class com.geekbrains.myweatherv3.WeatherFragment");
        }

        if (parcel.getCurrentFragmentName() != null) {
            String tag = parcel.getCurrentFragmentName();
            if(weatherFragment.getClass().toString().equals(tag)){
                setWeatherFragment();
                parcel.setCurrentFragmentName("class com.geekbrains.myweatherv3.WeatherFragment");
            } else if(citiesFragment.getClass().toString().equals(tag)){
                setCitiesFragment();
                parcel.setCurrentFragmentName("class com.geekbrains.myweatherv3.CitiesFragment");
            } else if(settingsFragment.getClass().toString().equals(tag)){
                setSettingsFragment();
                parcel.setCurrentFragmentName("class com.geekbrains.myweatherv3.SettingsFragment");
            } else if(developerFragment.getClass().toString().equals(tag)){
                setDeveloperFragment();
                parcel.setCurrentFragmentName("class com.geekbrains.myweatherv3.DeveloperFragment");
            } else if(feedbackFragment.getClass().toString().equals(tag)){
                setFeedbackFragment();
                parcel.setCurrentFragmentName("class com.geekbrains.myweatherv3.FeedbackFragment");
            }
        } else {
            setWeatherFragment();
        }

        setOnClickForSideMenuItems();
    }


    public void ToggleTheme(boolean isChecked){
        if (isChecked) {
            this.getTheme().applyStyle(R.style.AppDarkTheme, true);
            recreate();
        }
        else{
            this.getTheme().applyStyle(R.style.AppTheme, true);
            recreate();
        }
    }


    public static Parcel getParcel() {
        return parcel;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            navigationView.setCheckedItem(R.id.nav_weather);
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
        /*если нажата кнопка "разработчкики"*/
        if (item.getItemId() == R.id.action_yandex) {
            Uri uri = Uri.parse(findYandexWeatherHttp(parcel.getCityName()));
            Intent browser = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(browser);
        } else if (item.getItemId() == R.id.action_city_climate) {
            Uri uri = Uri.parse(findWikiWeatherHttp(parcel.getCityName()));
            Intent browser = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(browser);
        } else if (item.getItemId() == R.id.action_exit) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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

    private void setWeatherFragment() {
        setFragment(weatherFragment, "class com.geekbrains.myweatherv3.WeatherFragment");
    }

    private void setCitiesFragment() {
        setFragment(new CitiesFragment(), "class com.geekbrains.myweatherv3.CitiesFragment");
    }

    private void setSettingsFragment() {
        setFragment(settingsFragment, "class com.geekbrains.myweatherv3.SettingsFragment");
    }

    private void setDeveloperFragment() {
        setFragment(new DeveloperFragment(), "class com.geekbrains.myweatherv3.DeveloperFragment");
    }

    private void setFeedbackFragment() {
        setFragment(new FeedbackFragment(), "class com.geekbrains.myweatherv3.FeedbackFragment");
    }

    private void setFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}





