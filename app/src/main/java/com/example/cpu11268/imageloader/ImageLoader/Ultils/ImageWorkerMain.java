package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.graphics.Bitmap;

import com.example.cpu11268.imageloader.ImageLoader.ImageKey;
import com.example.cpu11268.imageloader.ImageLoader.ImageWorker;

import java.util.HashSet;

public class ImageWorkerMain {
    public static final int DEFAULT_MAX_SIZE = 0;
    public HashSet<ImageWorker.MyDownloadCallback> listCallback = new HashSet<>();
    public ImageKey imageKey;

    public ImageWorkerMain(ImageKey imageKey) {
        this.imageKey = imageKey;
    }

    public void onDownloadComplete(Bitmap bitmap, int resultCode) {
        if (listCallback != null) {
            for (ImageWorker.MyDownloadCallback callback : listCallback) {
                try {
                    callback.onLoad(bitmap, null, resultCode);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        listCallback.clear();
    }

    public interface MyDownloadCallback {
        void onLoad(Bitmap bitmap, Object which, int resultCode);
    }


}
