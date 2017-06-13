package com.example.anverhill.fragmentsapplication.Services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by anver.hill on 2017/06/10.
 */

public class DeviceConnectivityHelper {
    private static DeviceConnectivityHelper instance = null;
    private Context context;

    private DeviceConnectivityHelper() {
    }

    public static DeviceConnectivityHelper getInstance() {
        if (instance == null) {
            instance = new DeviceConnectivityHelper();
        }
        return instance;
    }
    public  void setContext(Context appContext)
    {
        context = appContext;
    }
    private ConnectivityManager createConnectivityManager()
    {
        if (context == null)
            return null;
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isInternetOn() {
        ConnectivityManager connectivityManager = createConnectivityManager();
        if(connectivityManager == null)
            return false;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
