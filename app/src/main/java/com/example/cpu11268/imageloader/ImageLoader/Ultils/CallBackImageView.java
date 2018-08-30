package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.cpu11268.imageloader.ImageLoader.ImageWorker;

public class CallBackImageView implements ImageWorker.MyDownloadCallback {

    private ImageView imageView;

    public CallBackImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void onLoad(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
