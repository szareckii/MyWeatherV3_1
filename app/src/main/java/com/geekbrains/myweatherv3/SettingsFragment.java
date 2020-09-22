package com.geekbrains.myweatherv3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.Objects;
import java.util.concurrent.Executor;

public class SettingsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener  {
    private Parcel parcel;
    private TextView textValueCountHoursBetweenForecasts;
    private SeekBar seekBarCountHoursBetweenForecasts;
    private static final String TAG = "myLogs";
    private int countHoursBetweenForecasts = 1;
    private CheckBox checkBoxSetVisibleWind;
    private CheckBox checkBoxSetVisiblePressure;
    private SwitchMaterial switchSetLightDarkTheme;
    private TextView welcomeTextView;
    // Используется, чтобы определить результат Activity регистрации через Google
    private static final int RC_SIGN_IN = 40404;

    // Клиент для регистрации пользователя через Google
    private GoogleSignInClient googleSignInClient;
    // Кнопка выхода из Google
    private MaterialButton buttonSingOut;

    // Кнопка регистрации через Google
    private com.google.android.gms.common.SignInButton buttonSignIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_settings, container, false);
        setRetainInstance(true);
        Log.d(TAG, "SettingsFragment- onCreate()");

        parcel = MainActivity.getParcel();
        parcel.setCurrentFragmentName(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentById(R.id.container)).getTag());

        findView(layout);
        textValueCountHoursBetweenForecasts.setText("1");
        seekBarCountHoursBetweenForecasts.setOnSeekBarChangeListener(this);

        setSettings();
        setDarkThemeClickBehavior();

        // Конфигурация запроса на регистрацию пользователя, чтобы получить
        // идентификатор пользователя, его почту и основной профайл (регулируется параметром)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        // Получаем клиента для регистрации и данные по клиенту
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        buttonSignIn.setOnClickListener(v -> signIn());
        buttonSingOut.setOnClickListener(v -> signOut());

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        enableSign();
        // Проверим, входил ли пользователь в это приложение через Google
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireActivity().getApplicationContext());
        if (account != null) {
            // Пользователь уже входил, сделаем кнопку недоступной
            disableSign();
            // Обновим ФИО этого пользователя и выведем его на экран
            updateUI(account.getFamilyName(), account.getGivenName());
        }
    }

    // Получаем результаты аутентификации от окна регистрации пользователя
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Когда сюда возвращается Task, результаты аутентификации уже
            // готовы
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // Инициируем регистрацию пользователя
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Получаем данные пользователя
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Регистрация прошла успешно
            disableSign();
            assert account != null;
            updateUI(account.getFamilyName(), account.getGivenName());

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure
            // reason. Please refer to the GoogleSignInStatusCodes class
            // reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    // Обновляем данные о пользователе на экране
    private void updateUI(String familyName, String givenName) {
        welcomeTextView.setVisibility(View.VISIBLE);
        if (familyName != null && givenName != null) {
            String fio = familyName + " " + givenName;
            welcomeTextView.setText(fio);
        }
    }

    // Выход из учётной записи в приложении
    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(task -> {
                    welcomeTextView.setVisibility(View.GONE);
                    enableSign();
                });
    }

    private void enableSign(){
        buttonSignIn.setEnabled(true);
        buttonSingOut.setEnabled(false);
    }

    private void disableSign(){
        buttonSignIn.setEnabled(false);
        buttonSingOut.setEnabled(true);
    }

    /*Метод переключения темы - светлая-темная*/
    private void setDarkThemeClickBehavior() {
        switchSetLightDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
        parcel.setDarkTheme(switchSetLightDarkTheme.isChecked());
        ((MainActivity) requireActivity()).ToggleTheme(switchSetLightDarkTheme.isChecked());
            } else {
        parcel.setDarkTheme(switchSetLightDarkTheme.isChecked());
        ((MainActivity) requireActivity()).ToggleTheme(switchSetLightDarkTheme.isChecked());
            }
        });
    }

    /*Метод выставления текущих настроек*/
    private void setSettings() {

        if (parcel.isVisibleWind()) {
            checkBoxSetVisibleWind.setChecked(true);
        } else {
            checkBoxSetVisibleWind.setChecked(false);
        }

        if(parcel.isVisiblePressure()) {
            checkBoxSetVisiblePressure.setChecked(true);
        } else {
            checkBoxSetVisiblePressure.setChecked(false);
        }

        if(parcel.isDarkTheme() ) {
            switchSetLightDarkTheme.setChecked(true);
        } else {
            switchSetLightDarkTheme.setChecked(false);
        }

        countHoursBetweenForecasts = parcel.getCountHoursBetweenForecasts();
        textValueCountHoursBetweenForecasts.setText(String.valueOf(countHoursBetweenForecasts));
        seekBarCountHoursBetweenForecasts.setProgress(countHoursBetweenForecasts - 1);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (checkBoxSetVisibleWind.isChecked()) {
            parcel.setVisibleWind(true);
        } else {
            parcel.setVisibleWind(false);
        }

        if (checkBoxSetVisiblePressure.isChecked()) {
            parcel.setVisiblePressure(true);
        } else {
            parcel.setVisiblePressure(false);
        }

        parcel.setCountHoursBetweenForecasts(countHoursBetweenForecasts);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    /*Т.к. у SeekBar минимальное значение 0 (до 26 API- ненастраиваемое), то делаем поправку на 1,
    чтобы минимальное значение получилось равное 1*/
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        textValueCountHoursBetweenForecasts.setText(String.valueOf(seekBar.getProgress() + 1));
        countHoursBetweenForecasts = seekBar.getProgress() + 1;
    }

    /*Метод инициализации полей из ресурсов*/
    private void findView(View layout) {
        textValueCountHoursBetweenForecasts = layout.findViewById(R.id.textValueCountHoursBetweenForecasts);
        seekBarCountHoursBetweenForecasts = layout.findViewById(R.id.seekBarCountHoursBetweenForecasts);
        checkBoxSetVisibleWind = layout.findViewById(R.id.checkBoxSetVisibleWind);
        checkBoxSetVisiblePressure = layout.findViewById(R.id.checkBoxSetVisiblePressure);
        switchSetLightDarkTheme = layout.findViewById(R.id.switchSetLightDarkTheme);
        buttonSignIn = layout.findViewById(R.id.sign_in_button);
        buttonSingOut = layout.findViewById(R.id.sing_out_button);
        welcomeTextView = layout.findViewById(R.id.welcomeTextView);

    }
}
