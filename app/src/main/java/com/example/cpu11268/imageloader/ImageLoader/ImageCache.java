package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.ValueBitmapMemCache;

public class ImageCache {
    public static final int DEFAULT_MAX_WIDTH_HEIGHT = 200;
    private static LruCache<String, ValueBitmapMemCache> mMemoryCache;
    private static LruCache<String, ValueBitmapMemCache> mMemoryCacheLarge;
    private final Object mDiskCacheLock = new Object();
    private DiskCacheSimple mDiskCacheSimple;
    private int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private int cacheSize = maxMemory / 8;

    private ImageCache(Context context) {

        mDiskCacheSimple = DiskCacheSimple.getInstance(context);

        mMemoryCache = new LruCache<String, ValueBitmapMemCache>(cacheSize) {
            @Override
            protected int sizeOf(String key, ValueBitmapMemCache value) {
                return value.getmBitmap().getByteCount();
            }
        };

        mMemoryCacheLarge = new LruCache<String, ValueBitmapMemCache>(cacheSize) {
            @Override
            protected int sizeOf(String key, ValueBitmapMemCache value) {
                return value.getmBitmap().getByteCount();
            }
        };

    }

    public static ImageCache getInstance(Context context) {
        return new ImageCache(context);
    }

    //MemoryCacheTotal
    public void addBitmapToMemoryCacheTotal(String key, ValueBitmapMemCache bitmap) {
        if (bitmap == null)
            return;
        if (bitmap.maxWidthHeight() > DEFAULT_MAX_WIDTH_HEIGHT) {
            addBitmapToMemoryLargeCache(key, bitmap);
        } else {
            addBitmapToMemoryCache(key, bitmap);
        }
    }

    //*

    // Memory Cache
    public void addBitmapToMemoryCache(String key, ValueBitmapMemCache bitmap) {
        if (!isBitmapFromMemoryCache(key) && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public ValueBitmapMemCache getBitmapFromMemoryCache(String key) {
        return mMemoryCache != null ? mMemoryCache.get(key) : null;
    }

    public boolean isBitmapFromMemoryCache(String key) {
        return mMemoryCache.snapshot().containsKey(key);
    }
    //*

    //Memory Large Cache
    public void addBitmapToMemoryLargeCache(String key, ValueBitmapMemCache bitmap) {
        if (!isBitmapFromMemoryLargeCache(key) && bitmap != null) {
            mMemoryCacheLarge.put(key, bitmap);
        }
    }

    public ValueBitmapMemCache getBitmapFromMemoryLargeCache(String key) {
        return mMemoryCacheLarge != null ? mMemoryCacheLarge.get(key) : null;
    }

    public boolean isBitmapFromMemoryLargeCache(String key) {
        return mMemoryCacheLarge.snapshot().containsKey(key);
    }
    //*

    // Disk Cache
    public void addBitmapToDiskCache(String key, byte[] bytes) {
        synchronized (mDiskCacheLock) {
            if (mDiskCacheSimple != null && mDiskCacheSimple.get(key) == null) {
                mDiskCacheSimple.put(key, bytes);
            }
        }
    }

    public boolean isBitmapFromDiskCache(String key) {
        return mDiskCacheSimple.isBitmapFromDisk(key);
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        synchronized (mDiskCacheLock) {
            return mDiskCacheSimple != null ? mDiskCacheSimple.get(key) : null;
        }
    }

    public Bitmap getBitmapFromDiskCache(String key, int width, int height, BitmapFactory.Options options) {
        synchronized (mDiskCacheLock) {
            return mDiskCacheSimple != null ? mDiskCacheSimple.get(key, width, height, options) : null;
        }
    }

    //*


}
