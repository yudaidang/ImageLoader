package com.example.cpu11268.imageloader.ImageLoader;

/**
 * Created by hungnq3 on 05/09/18.
 */
public class ImageKey {
    private String mUrl;
    private int mWidth;
    private int mHeight;

    public ImageKey(String url, int width, int height) {
        mUrl = url;
        int sampleSize = sampleSize(width, height);
        if (sampleSize <= 0){
            mWidth = 0;
            mHeight = 0;
        }else if(sampleSize <= 64){
            mWidth = 64;
            mHeight = 64;
        }else if(sampleSize <= 128){
            mWidth = 128;
            mHeight = 128;
        }else if(sampleSize <= 256){
            mWidth = 256;
            mHeight = 256;
        }else if(sampleSize <= 512){
            mWidth = 512;
            mHeight = 512;
        }else{
            mWidth = width;
            mHeight = height;
        }
    }

    private int sampleSize(int width, int height) {
        return width > height ? width : height;
    }

    public String getmUrl() {
        return mUrl;
    }


    public int getmWidth() {
        return mWidth;
    }


    public int getmHeight() {
        return mHeight;
    }


    @Override
    public int hashCode() {
        return (mUrl.hashCode() + sampleSize(mWidth, mHeight));
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.hashCode() == this.hashCode();
    }
}
