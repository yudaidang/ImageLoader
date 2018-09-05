package com.example.cpu11268.imageloader.ImageLoader;

/**
 * Created by hungnq3 on 05/09/18.
 */
public class ImageKey {
    String mUrl;
    int mWidth;
    int mHeight;

    public ImageKey(String url, int width, int height) {
        mUrl = url;
        mWidth = width;
        mHeight = height;
    }


    @Override
    public int hashCode() {
        return (super.hashCode() * 31 + mWidth);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.hashCode() == this.hashCode();
    }
}
