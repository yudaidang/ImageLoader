package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;

public class ValueBitmapMemCache {
    private Bitmap mBitmap;
    private boolean mMaxSize;

    public ValueBitmapMemCache(Bitmap bitmap, boolean mMaxSize) {
        this.mBitmap = bitmap;
        this.mMaxSize = mMaxSize;
    }

    public boolean isMaxSize() {
        return mMaxSize;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int getWidth() {
        return mBitmap != null ? mBitmap.getWidth() : 0;
    }

    public int getHeight() {
        return mBitmap != null ? mBitmap.getHeight() : 0;
    }
}
