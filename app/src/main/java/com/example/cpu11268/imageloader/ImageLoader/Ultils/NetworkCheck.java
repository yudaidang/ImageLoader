package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.ref.WeakReference;

public class NetworkCheck {
    private WeakReference<Context> context; //? keep instance?

    private NetworkCheck(Context context) {
        this.context = new WeakReference<>(context);
    }

    public static NetworkCheck getInstance(Context context){
        return new NetworkCheck(context);
    }

    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return (networkInfo != null && networkInfo.isConnected());
    }
}
