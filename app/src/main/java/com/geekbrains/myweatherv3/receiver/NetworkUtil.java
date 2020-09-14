package com.geekbrains.myweatherv3.receiver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static int TYPE_WIFI = 1;
    public static int TYPE_NOT_CONNECTED = 0;

    public static int getConnectivityStatus(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getConnectivityStatusString(Context context) {

        int conn = NetworkUtil.getConnectivityStatus(context);

        String statusNetwork = null;
        if (conn == NetworkUtil.TYPE_WIFI) {
            //statusNetwork = "Wifi enabled";
            statusNetwork = "Internet connection available";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            statusNetwork = "Not connected to Internet";
        }
        return statusNetwork;
    }
}
