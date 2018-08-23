package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import com.example.cpu11268.imageloader.ImageLoader.ImageCache;

public class AddImageRunnable implements Runnable  {

    private ImageCache mImageCache;
    private String mImageUrl;
    private byte[] mBytes;

    public AddImageRunnable(ImageCache mImageCache, String mImageUrl, byte[] mBytes) {
        this.mImageCache = mImageCache;
        this.mImageUrl = mImageUrl;
        this.mBytes = mBytes;
    }

    @Override
    public void run() {
        mImageCache.addBitmapToDiskCache(mImageUrl, mBytes);
    }
}
