package com.android.tumb.Misc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import com.android.tumb.Main.MainActivity;

/**
 * Created by trust on 8/28/2016.
 */
public class InternetReceiver extends BroadcastReceiver{
    MainActivity activity;

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            informNetworkChange(true);
        }
        else{
            informNetworkChange(false);
        }
    }

    public void setMainActivityHandler(MainActivity mainActivity){
        activity = mainActivity;
    }

    public void informNetworkChange(Boolean bool){
        activity.networkChange(bool);
    }
}
