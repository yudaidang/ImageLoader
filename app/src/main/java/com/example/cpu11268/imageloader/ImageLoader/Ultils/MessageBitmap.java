package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.graphics.Bitmap;

public class MessageBitmap {
    private Bitmap mBitmap;
    private String mUrl;
    private boolean mMaxSize;

    public MessageBitmap(String mUrl, Bitmap mBitmap, boolean mMaxSize) {
        this.mMaxSize = mMaxSize;
        this.mBitmap = mBitmap;
        this.mUrl = mUrl;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public String getmUrl() {
        return mUrl;
    }

    public boolean ismMaxSize() {
        return mMaxSize;
    }
}
