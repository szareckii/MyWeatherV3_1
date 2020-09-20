package com.geekbrains.myweatherv3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.geekbrains.myweatherv3.model.SearchRequest;
import com.geekbrains.myweatherv3.receiver.WiFiChangeReceiver;
import com.geekbrains.myweatherv3.weatherdata.CityDataOnlyNeed;
import com.geekbrains.myweatherv3.weatherdata.RetrofitAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
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
    private MapFragment mapFragment;
    private static Parcel parcel;
    private Pattern checkCityName = Pattern.compile("^[а-яА-ЯёЁa-zA-Z0-9`']+$");
    private final String TAG_WEATHER = "class com.geekbrains.myweatherv3.WeatherFragment";
    private final String TAG_CITIES = "class com.geekbrains.myweatherv3.CitiesFragment";
    private final String TAG_SETTINGS = "class com.geekbrains.myweatherv3.SettingsFragment";
    private final String TAG_DEVELOPER = "class com.geekbrains.myweatherv3.DeveloperFragment";
    private final String TAG_MAP = "class com.geekbrains.myweatherv3.MapFragment";
    private String cityName;
    private Stack<Fragment> fragmentStack;
    private OpenWeather openWeather;
    private String msgError;
    private SharedPreferences sharePref;
    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_NAME = "currentCity";
    private static final String APP_PREFERENCES_LON = "lon";
    private static final String APP_PREFERENCES_LAT = "lat";
    private static final String APP_PREFERENCES_VISIT = "hasVisited";
    private final static String BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private WiFiChangeReceiver messageReceiver;
    private static final int PERMISSION_REQUEST_CODE = 10;
    private LocationManager locationManager = null;
    private final static String MSG_NO_DATA = "No data";
    private LocListener mLocListener = null;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDarkTheme();
        setContentView(R.layout.activity_main);

        initView();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        RetrofitAdapter retrofitAdapter = new RetrofitAdapter();
        openWeather = retrofitAdapter.getOpenWeather();

        createNewFragment();

        if (parcel == null) {
            parcel = new Parcel("Москва", true, true,
                    1, false, 37.62f, 55.75f, TAG_WEATHER);
        }

        initBroadcastReceiver();

        getSharePref();

        setOnClickForSideMenuItems();

        initGetToken();
        initNotificationChannel();
    }

    //Инициализация View
    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        msgError = getString(R.string.check_cityname);
        navigationView.setCheckedItem(R.id.nav_weather);
        fragmentStack = new Stack<>();
    }

    //Светлая-темная тема
    private void setDarkTheme() {
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
    }

    private void createNewFragment() {
        weatherFragment = new WeatherFragment();
        citiesFragment = new CitiesFragment();
        settingsFragment = new SettingsFragment();
        developerFragment = new DeveloperFragment();
        mapFragment = new MapFragment();
    }

    //Метод регистрации BroadcastReceiver
    private void initBroadcastReceiver() {

        messageReceiver  = new WiFiChangeReceiver();
        // создаем фильтр для BroadcastReceiver
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);

        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(messageReceiver, intFilt);
    }

    private void initGetToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("PushMessage", "getInstanceId failed", task.getException());
                    }
                });
    }

    // инициализация канала нотификаций
    private void initNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("3", "Messages", importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // дерегистрируем (выключаем) BroadcastReceiver
        unregisterReceiver(messageReceiver);
    }

    //Метод получения данных из SharePreference
    private void getSharePref() {
        sharePref = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);
        boolean hasVisited = sharePref.getBoolean(APP_PREFERENCES_VISIT, false);

        if (hasVisited) {
            parcel.setCityName(sharePref.getString(APP_PREFERENCES_NAME,    ""));
            parcel.setLon(sharePref.getFloat(APP_PREFERENCES_LON, 0f));
            parcel.setLat(sharePref.getFloat(APP_PREFERENCES_LAT, 0f));
            Log.e(TAG, "SharedPreferences. GET: " + parcel.getCityName());
            setFragment();
        } else {
            requestPermissions();
        }
    }

    // Сохраняем настройки
    private void savePreferences(SharedPreferences sharedPref){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(APP_PREFERENCES_NAME, parcel.getCityName());
        editor.putFloat(APP_PREFERENCES_LON, parcel.getLon());
        editor.putFloat(APP_PREFERENCES_LAT, parcel.getLat());
        editor.putBoolean(APP_PREFERENCES_VISIT, true);
        editor.apply();
        Log.e(TAG, "SharedPreferences. PUT: " + parcel.getCityName());
    }

    //Метод выбора нужного фрагмента
    public void setFragment() {
        setWeatherFragment();
        if (parcel.getCurrentFragmentName() != null) {
            String tag = parcel.getCurrentFragmentName();
            if(citiesFragment.getClass().toString().equals(tag)){
                setCitiesFragment();
            } else if(settingsFragment.getClass().toString().equals(tag)){
                setSettingsFragment();
            } else if(developerFragment.getClass().toString().equals(tag)){
                setDeveloperFragment();
            } else if(mapFragment.getClass().toString().equals(tag)){
                setMapFragment();
            }
        }
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
                case TAG_MAP : {
                    navigationView.setCheckedItem(R.id.nav_map);
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
                        cityName = editText.getText().toString().trim();
                        if (checkCityName.matcher(cityName).matches()) {    // Проверим на основе регулярных выражений
                            requestRetrofit(cityName);
                        } else {
                            showToast(msgError);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        /*если нажата кнопка "текущая позиция"*/
        } else if (item.getItemId() == R.id.action_current_position) {
            requestPermissions();
            setWeatherFragment();
        }
        else if (item.getItemId() == R.id.action_yandex) {
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
        String lang = Locale.getDefault().getLanguage();
        openWeather.loadCityCoord(city, BuildConfig.WEATHER_API_KEY, lang)
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
                case R.id.nav_map: {
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        setMapFragment();
                        drawer.close();
                    } else {
                        View view = findViewById(android.R.id.content);
                        Snackbar.make(view, R.string.not_supportet,  Snackbar.LENGTH_LONG)
                                .setAction(R.string.ok_button, v ->
                                        Log.d(TAG, "Build.VERSION.SDK_INT = Build.VERSION_CODES.LOLLIPOP")).show();
                    }
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

    private void setMapFragment() {
        Log.e(TAG, "setMapFragment");
        mapFragment = new MapFragment();
        setFragment(mapFragment, TAG_MAP);
        fragmentStack.push(mapFragment);
    }

    private void setFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // Запрашиваем Permission’ы
    private void requestPermissions() {
        // Проверим, есть ли Permission’ы, и если их нет, запрашиваем их у
        // пользователя
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission’ов нет, запрашиваем их у пользователя
            requestLocationPermissions();
        } else {
            // Запрашиваем координаты
            requestLocation();
        }
    }

    private void requestLocation() {
        // Если Permission’а всё-таки нет, просто выходим: приложение не имеет
        // смысла
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        // Получаем менеджер геолокаций
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

         if ( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER ) ) {
             //Вывод окна включения геолокации
             Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
             startActivity(intent);
         }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        // Получаем наиболее подходящий провайдер геолокации по критериям.
        // Но определить, какой провайдер использовать, можно и самостоятельно.
        // В основном используются LocationManager.GPS_PROVIDER или
        // LocationManager.NETWORK_PROVIDER, но можно использовать и
        // LocationManager.PASSIVE_PROVIDER - для получения координат в
        // пассивном режиме
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            if (mLocListener == null) {
                mLocListener = new LocListener();
            }

            locationManager.requestLocationUpdates(provider, 10000, 10,  mLocListener);
        }
    }

    // Запрашиваем Permission’ы для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            // Запрашиваем эти два Permission’а у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    // Результат запроса Permission’а у пользователя:
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {   // Запрошенный нами
            // Permission
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                // Все препоны пройдены и пермиссия дана
                // Запросим координаты
                requestLocation();
            }
        }
    }

    private final class LocListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged: " + location.toString());
            float lat = (float) location.getLatitude(); // Широта

            float lng = (float) location.getLongitude(); // Долгота

            String accuracy = Float.toString(location.getAccuracy());   // Точность
            Log.d(TAG, "LatLng currentPosition = new LatLng(lat, lng);");
            Log.d(TAG, "Lat   Lng " + lat + " " + lng);

            parcel.setCityName(getAddressByLoc(lat, lng));
            parcel.setLat(lat);
            parcel.setLon(lng);

            setFragment();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { /* Empty */ }
        @Override
        public void onProviderEnabled(@NonNull String provider) { /* Empty */ }
        @Override
        public void onProviderDisabled(@NonNull String provider) { /* Empty */ }
    }

    //Получаем название города по координатам
    @SuppressLint("DefaultLocale")
    public String getAddressByLoc(float lat, float lon) {

        final Geocoder geo = new Geocoder(this);

        List<Address> list;
        try {
            list = geo.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }

        if (list.isEmpty()) return MSG_NO_DATA;

        Address a = list.get(0);

        if (a.getLocality() != null) {
            return a.getLocality();
        } else {
            return "(" + String.format("%.2f", lat) + "; " +  String.format("%.2f", lon) + ")";
        }
    }

    //удаляем листнер на передвижение
    @Override
    protected void onPause() {
        if (mLocListener != null) {
            locationManager.removeUpdates(mLocListener);
        }
        super.onPause();
    }

}








