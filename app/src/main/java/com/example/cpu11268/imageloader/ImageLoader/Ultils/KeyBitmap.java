package com.example.cpu11268.imageloader.ImageLoader.Ultils;

public class KeyBitmap {
    private int mSampleSize;
    private String mUrl;

    public KeyBitmap(int mSampleSize, String mUrl) {
        this.mSampleSize = mSampleSize;
        this.mUrl = mUrl;
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

    @Override
    public int hashCode() {
        return (mUrl.hashCode() * 31 + mSampleSize);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.hashCode() == this.hashCode();
    }

}
