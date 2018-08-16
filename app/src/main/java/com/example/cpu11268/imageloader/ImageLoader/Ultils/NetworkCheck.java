package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCheck {
    private Context context; //? keep instance?

    private NetworkCheck(Context context) {
        this.context = context.getApplicationContext();
    }

    public static NetworkCheck getInstance(Context context){
        return new NetworkCheck(context);
    }

    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
