package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.cpu11268.imageloader.ImageLoader.ImageWorker;

import java.lang.ref.WeakReference;

public class CallBackImageView implements ImageWorker.MyDownloadCallback {

    private WeakReference<ImageView> imageView;

    public CallBackImageView(ImageView imageView) {
        this.imageView = new WeakReference<>(imageView);
    }

    @Override
    public void onLoad(Bitmap bitmap) {
        imageView.get().setImageBitmap(bitmap);
    }
}