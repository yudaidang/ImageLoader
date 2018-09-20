package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;

public class ValueBitmapMemCache {
    private Bitmap mBitmap;
    private boolean mMaxSize;

    public ValueBitmapMemCache(Bitmap mBitmap, boolean mMaxSize) {
        this.mBitmap = mBitmap;
        this.mMaxSize = mMaxSize;
    }

    public boolean isMaxSize() {
        return mMaxSize;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

}
