package com.geekbrains.myweatherv3.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.geekbrains.myweatherv3.MainActivity;
import com.geekbrains.myweatherv3.R;

public class WiFiChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "myLogs";
    private NotificationManager notificationManager;
    private static boolean statusConnection = false;

    // Сюда приходит широковещательное оповещение
    @Override
    public void onReceive(Context context, Intent intent) {
        initNotification(context);

        String status = NetworkUtil.getConnectivityStatusString(context);
        Log.e(TAG, "WiFiChangeReceiver: " + status);

        if (status.equals("Not connected to Internet")) {
            Log.e(TAG, "not connection");
            statusConnection = false;
            setNotification(context);
            setAlertConnection(context);
        } else {
            Log.e(TAG, "connected to internet");
            statusConnection = true;
            notificationManager.cancelAll();
        }
    }

    public boolean isStatusConnection() {
        return statusConnection;
    }

    private void setNotification(Context context) {
        // Создание PendingIntent
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "2")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(context.getString(R.string.internet_connection_status_false))
                        .setContentText(context.getString(R.string.weather_update_status))
                        .setContentIntent(resultPendingIntent);

        notificationManager.notify(1, builder.build());
    }

    private void initNotification(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("2", "Connection", importance);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //Показать Alert - отсутствие сети
    public void setAlertConnection(Context context) {
        // Создаём билдер и передаём контекст приложения
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // В билдере указываем заголовок окна (можно указывать как ресурс,
        // так и строку)
        builder.setTitle(R.string.exclamation)
                // Указываем сообщение в окне
                .setMessage(R.string.internet_connection_status_false)
                // Можно указать и пиктограмму
                .setIcon(R.mipmap.ic_launcher_round)
                // Из этого окна нельзя выйти кнопкой Back
                .setCancelable(true)
                .setNeutralButton(R.string.turn_on_wifi,
                        // Ставим слушатель, нажатие будем обрабатывать
                        (dialog, id) -> {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            context.startActivity(intent);
//                                Toast.makeText(context, "Кнопка нажата", Toast.LENGTH_SHORT).show();
                        })
                // Устанавливаем кнопку
                .setPositiveButton(R.string.ok_button,
                        // Ставим слушатель, нажатие будем обрабатывать
                        (dialog, id) -> {
//                                Toast.makeText(context, "Кнопка нажата", Toast.LENGTH_SHORT).show();
                        });
        AlertDialog alert = builder.create();
        alert.show();
//        Toast.makeText(context, "Диалог открыт", Toast.LENGTH_SHORT).show();
    }


}

