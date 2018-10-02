package com.example.cpu11268.imageloader.ImageLoader;

/**
 * Created by hungnq3 on 05/09/18.
 */
public class ImageKey {
    private String mUrl;
    private int mSize;
    private final int DEFAULT_OUT_WIDTH_HEIGHT = 0;
    private int mOutWidth;
    private int mOutHeight;


    public ImageKey(String url, int width, int height) {
        this.mOutHeight = DEFAULT_OUT_WIDTH_HEIGHT;
        this.mOutWidth = DEFAULT_OUT_WIDTH_HEIGHT;
        mUrl = url;
        int sampleSize = sampleSize(width, height);
        if (sampleSize <= 0) {
            mSize = 0;
        } else if (sampleSize <= 64) {
            mSize = 64;
        } else if (sampleSize <= 128) {
            mSize = 128;
        } else if (sampleSize <= 256) {
            mSize = 256;
        } else if (sampleSize <= 512) {
            mSize = 512;
        } else {
            mSize = width;
        }
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

    private int sampleSize(int width, int height) {
        return width > height ? width : height;
    }

    public String getmUrl() {
        return mUrl;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int mSize) {
        this.mSize = mSize;
    }

    @Override
    public int hashCode() {
        return (mUrl.hashCode() + mSize);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.hashCode() == this.hashCode();
    }

}
