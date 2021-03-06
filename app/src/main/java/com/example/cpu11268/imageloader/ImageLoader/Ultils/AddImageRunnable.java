package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import com.example.cpu11268.imageloader.ImageLoader.ImageCache;

public class AddImageRunnable implements Runnable {

    private String mImageUrl;
    private byte[] mBytes;
    private String diskPath;

    public AddImageRunnable(String mImageUrl, byte[] mBytes, String diskPath) {
        this.mImageUrl = mImageUrl;
        this.mBytes = mBytes;
        this.diskPath = diskPath;
    }

    @Override
    public void run() {
        ImageCache.getInstance().addBitmapToDiskCache(mImageUrl, mBytes, diskPath);
    }
}
