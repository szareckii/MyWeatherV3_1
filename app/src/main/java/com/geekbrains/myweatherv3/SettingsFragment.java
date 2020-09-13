package com.geekbrains.myweatherv3;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.Objects;

public class SettingsFragment extends Fragment implements SeekBar.OnSeekBarChangeListener  {
    private Parcel parcel;
    private TextView textValueCountHoursBetweenForecasts;
    private SeekBar seekBarCountHoursBetweenForecasts;
    private static final String TAG = "myLogs";
    private int countHoursBetweenForecasts = 1;
    private CheckBox checkBoxSetVisibleWind;
    private CheckBox checkBoxSetVisiblePressure;
    private SwitchMaterial switchSetLightDarkTheme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_settings, container, false);
        setRetainInstance(true);

        parcel = MainActivity.getParcel();
        parcel.setCurrentFragmentName(Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentById(R.id.container)).getTag());

        findView(layout);
        textValueCountHoursBetweenForecasts.setText("1");

        Log.d(TAG, "SettingsFragment- onCreate()");

        seekBarCountHoursBetweenForecasts.setOnSeekBarChangeListener(this);

        setSettings();
        setDarkThemeClickBehavior();

        return layout;
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
    }
}
