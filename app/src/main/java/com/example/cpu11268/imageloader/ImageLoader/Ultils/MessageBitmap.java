package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.graphics.Bitmap;

public class MessageBitmap {
    private Bitmap mBitmap;
    private String mUrl;
    private boolean mMaxSize;
    private String id;

    public MessageBitmap(String mUrl, Bitmap mBitmap, boolean mMaxSize) {
        this.mMaxSize = mMaxSize;
        this.mBitmap = mBitmap;
        this.mUrl = mUrl;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
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
