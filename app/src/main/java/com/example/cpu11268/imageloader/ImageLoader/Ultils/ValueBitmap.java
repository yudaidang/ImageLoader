package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.graphics.Bitmap;

public class ValueBitmap {
    private Bitmap mBitmap;
    private int mSampleSize;
    private String mUrl;
    private int mOutWidth;
    private int mOutHeight;

    public ValueBitmap(Bitmap mBitmap, int mSampleSize, String mUrl, int mOutWidth, int mOutHeight) {
        this.mBitmap = mBitmap;
        this.mSampleSize = mSampleSize;
        this.mUrl = mUrl;
        this.mOutWidth = mOutWidth;
        this.mOutHeight = mOutHeight;
    }

    public Bitmap getmBitmap() {
            return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public int getmSampleSize() {
        return mSampleSize;
    }

    public void setmSampleSize(int mSampleSize) {
        this.mSampleSize = mSampleSize;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public int getmOutWidth() {
        return mOutWidth;
    }

    public void setmOutWidth(int mOutWidth) {
        this.mOutWidth = mOutWidth;
    }

    public int getmOutHeight() {
        return mOutHeight;
    }

    public void setmOutHeight(int mOutHeight) {
        this.mOutHeight = mOutHeight;
    }
}
