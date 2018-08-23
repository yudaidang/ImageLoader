package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

public class ImageCache {
    private static LruCache<String, Bitmap> mMemoryCache;
    private final Object mDiskCacheLock = new Object();
    private DiskCacheSimple mDiskCacheSimple;
    private int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private int cacheSize = maxMemory / 20;

    private ImageCache (Context context){

        mDiskCacheSimple = DiskCacheSimple.getInstance(context);

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

    }

    public static ImageCache getInstance(Context context){
        ImageCache imageCache = new ImageCache(context);
        return imageCache;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemoryCache(key) == null && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }

    }

    public void addBitmapToDiskCache(String key, byte[] bytes) {
        synchronized (mDiskCacheLock) {
            if (mDiskCacheSimple != null && mDiskCacheSimple.get(key) == null) {
                mDiskCacheSimple.put(key, bytes);
            }
        }
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        Bitmap bitmap = null;
        if (mMemoryCache != null) {
            bitmap = mMemoryCache.get(key);
        }
        return bitmap;
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        synchronized (mDiskCacheLock) {
            if (mDiskCacheSimple != null) {
                return mDiskCacheSimple.get(key);
            }
        }
        return null;
    }

    public Bitmap getBitmapFromDiskCache(String key, int width, int height, BitmapFactory.Options options) {
        synchronized (mDiskCacheLock) {
            if (mDiskCacheSimple != null) {
                return mDiskCacheSimple.get(key, width, height, options);
            }
        }
        return null;
    }



}
