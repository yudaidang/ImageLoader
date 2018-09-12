package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import com.example.cpu11268.imageloader.ImageLoader.ImageCache;

public class AddImageRunnable implements Runnable {

    private String mImageUrl;
    private byte[] mBytes;

    public AddImageRunnable(String mImageUrl, byte[] mBytes) {
        this.mImageUrl = mImageUrl;
        this.mBytes = mBytes;
    }

    @Override
    public void run() {
        ImageCache.getInstance().addBitmapToDiskCache(mImageUrl, mBytes);
    }
}
