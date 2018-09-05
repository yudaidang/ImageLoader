package com.example.cpu11268.imageloader;

import android.app.Application;
import android.util.DisplayMetrics;

import com.example.cpu11268.imageloader.ImageLoader.ImageWorker;
import com.squareup.leakcanary.LeakCanary;

public class MemoryLeakApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }


        DisplayMetrics display = getApplicationContext().getResources().getDisplayMetrics();

        int mMaxSizeMem = (int) (
                (int) display.widthPixels  * display.heightPixels * 4);// ARGB: 4, RGB: 3, BMP: 16, BMPS: 32

        ImageWorker.getInstance().setSizeLargeMemCache(mMaxSizeMem); //?
        ImageWorker.getInstance().setSizeLargeMemCache(mMaxSizeMem); //?

        LeakCanary.install(this);
    }
}
