package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.graphics.Bitmap;

public class ValueBitmapMemCache {
    Bitmap mBitmap;
    int mWidth;
    int mHeight;

    public ValueBitmapMemCache(Bitmap bitmap, int mWidth, int mHeight) {
        this.mBitmap = bitmap;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    public int maxWidthHeight() {
        return mWidth > mHeight ? mWidth : mHeight;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }
}
