package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.graphics.Bitmap;

import com.example.cpu11268.imageloader.ImageLoader.ImageKey;

public class MessageBitmap {
    private Bitmap mBitmap;
    private ImageKey imageKey;
    private String id;

    public MessageBitmap(ImageKey imageKey, Bitmap mBitmap) {
        this.mBitmap = mBitmap;
        this.imageKey = imageKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public ImageKey getImageKey() {
        return imageKey;
    }
}
