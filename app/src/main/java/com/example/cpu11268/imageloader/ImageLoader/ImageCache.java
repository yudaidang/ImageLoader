package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

public class ImageCache {
    public static final int DEFAULT_MAX_SIZE = 66000; //?
    private static LruCache<ImageKey, ValueBitmapMemCache> mMemoryCache;
    private static LruCache<ImageKey, ValueBitmapMemCache> mMemoryCacheLarge;
    private static ImageCache sInstance = new ImageCache();

    private int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private int cacheSize = maxMemory / 8;

    private ImageCache() { //?

        mMemoryCache = new LruCache<ImageKey, ValueBitmapMemCache>(cacheSize) {
            @Override
            protected int sizeOf(ImageKey key, ValueBitmapMemCache value) {
                return value.getBitmap().getByteCount() / 1024;

            }
        };

        mMemoryCacheLarge = new LruCache<ImageKey, ValueBitmapMemCache>(cacheSize) {
            @Override
            protected int sizeOf(ImageKey key, ValueBitmapMemCache value) {
                return value.getBitmap().getByteCount() / 1024;
            }
        };

    }

    public static ImageCache getInstance() { //?
        return sInstance;
    }

    public void setSizeLargeMem(int mMaxSize) {
        mMemoryCacheLarge.resize(mMaxSize);
    }

    public void setSizeSmallMem(int mMaxSize) {
        mMemoryCache.resize(mMaxSize);
    }

    //MemoryCacheTotal
    public synchronized void addBitmapToMemoryCacheTotal(ImageKey key, ValueBitmapMemCache bitmap) {
        if (bitmap == null)
            return;
        if (key.getmHeight() * key.getmWidth() > DEFAULT_MAX_SIZE) {
            addBitmapToMemoryLargeCache(key, bitmap);
        } else {
            addBitmapToMemoryCache(key, bitmap);
        }
    }

    //*

    public Bitmap findBitmapCache(String url, int width, int height) {
        ImageKey imageKey = new ImageKey(url, width, height);
        if (isBitmapFromMemoryCache(imageKey)) {
            return getBitmapFromCache(mMemoryCache, imageKey).getBitmap();
        } else if (isBitmapFromMemoryLargeCache(imageKey)) {
            return getBitmapFromCache(mMemoryCacheLarge, imageKey).getBitmap();
        } else {
            return null;
        }
    }

    private ValueBitmapMemCache getBitmapFromCache(LruCache<ImageKey, ValueBitmapMemCache> cache, ImageKey key) {
        if (key != null) {
            return cache.get(key);
        }
        return null;
    }

    // Memory Cache
    private void addBitmapToMemoryCache(ImageKey key, ValueBitmapMemCache bitmap) { //?
        if (!isBitmapFromMemoryCache(key) && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private boolean isBitmapFromMemoryCache(ImageKey key) { //?
        return mMemoryCache.snapshot().containsKey(key);
    }
    //*

    //Memory Large Cache
    private void addBitmapToMemoryLargeCache(ImageKey key, ValueBitmapMemCache bitmap) { //?
        if (!isBitmapFromMemoryLargeCache(key) && bitmap != null) {
            mMemoryCacheLarge.put(key, bitmap);
        }
    }

    private boolean isBitmapFromMemoryLargeCache(ImageKey key) { //?
        return mMemoryCacheLarge.snapshot().containsKey(key);
    }
    //*

    // Disk Cache
    public synchronized void addBitmapToDiskCache(String key, byte[] bytes) {
        if (DiskCacheSimple.getInstance().get(key) == null) {
            DiskCacheSimple.getInstance().put(key, bytes);
        }
    }

    public boolean isBitmapFromDiskCache(String key) { //?
        return DiskCacheSimple.getInstance().isExistFile(key);
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        return DiskCacheSimple.getInstance().get(key);
    }

    public Bitmap getBitmapFromDiskCache(String key, int width, int height, BitmapFactory.Options options) {
        return DiskCacheSimple.getInstance().get(key, width, height, options); //?
    }

    //*


}
