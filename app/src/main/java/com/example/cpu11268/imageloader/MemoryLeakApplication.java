package com.example.cpu11268.imageloader;

import android.app.Application;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.example.cpu11268.imageloader.ImageLoader.DiskCacheSimple;
import com.example.cpu11268.imageloader.ImageLoader.ImageCache;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;
import java.util.Objects;

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
                (int) (display.widthPixels / getResources().getDisplayMetrics().density)
                        * (display.heightPixels / getResources().getDisplayMetrics().density) * 4);// ARGB: 4, RGB: 3, BMP: 16, BMPS: 32

        ImageCache.getInstance().setSizeLargeMem(mMaxSizeMem);
        ImageCache.getInstance().setSizeSmallMem(mMaxSizeMem);

        LeakCanary.install(this);

        File diskCacheDir = getDiskCacheDir("IMAGE");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }


        DiskCacheSimple.getInstance().setListFile(diskCacheDir);
    }

    private File getDiskCacheDir(String uniqueName) {
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ?
                        Objects.requireNonNull(getExternalCacheDir()).getPath() :
                        getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }

}
