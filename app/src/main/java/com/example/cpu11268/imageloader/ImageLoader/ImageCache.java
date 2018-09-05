package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.LruCache;

public class ImageCache {
    public static final int DEFAULT_MAX_SIZE = 40000; //?
    private static LruCache<String, ValueBitmapMemCache> mMemoryCache;
    private static LruCache<String, ValueBitmapMemCache> mMemoryCacheLarge;
    private final Object mDiskCacheLock = new Object();
    private int maxMemory = (int) Runtime.getRuntime().maxMemory();
    private int cacheSize = maxMemory / 8;


    private static ImageCache sInstance = new ImageCache();

    public static ImageCache getInstance() { //?
        return sInstance;
    }


    private ImageCache() { //?

        mMemoryCache = new LruCache<String, ValueBitmapMemCache>(cacheSize) {
            @Override
            protected int sizeOf(String key, ValueBitmapMemCache value) {
                return value.getBitmap().getByteCount();
            }
        };

        mMemoryCacheLarge = new LruCache<String, ValueBitmapMemCache>(cacheSize) {
            @Override
            protected int sizeOf(String key, ValueBitmapMemCache value) {
                return value.getBitmap().getByteCount();
            }
        };

    }


    public void setSizeLargeMem(int mMaxSize) {
        mMemoryCacheLarge.resize(mMaxSize);
    }

    public void setSizeSmallMem(int mMaxSize) {
        mMemoryCache.resize(mMaxSize);
    }

    //MemoryCacheTotal
    public void addBitmapToMemoryCacheTotal(String key, ValueBitmapMemCache bitmap) {
        if (bitmap == null)
            return;

        if (bitmap.getHeight() * bitmap.getWidth() > DEFAULT_MAX_SIZE) {
            addBitmapToMemoryLargeCache(key, bitmap);
        } else {
            addBitmapToMemoryCache(key, bitmap);
        }
    }

    //*

    public Bitmap findBitmapCache(String url, int width, int height) {
        return mCache.get(new ImageKey(mUrl, width, height));

        if (!TextUtils.isEmpty(url)) {
            ValueBitmapMemCache valueBitmapMemCache = getBitmapFromCache(mMemoryCache, url);
            // Nếu valueBitmapMemCache đã lưu trong memcache chưa phải là valueBitmapMemCache có size lớn nhất và width * height lớn hơn default size.
            // width * height : lấy valueBitmapMemCache gốc.
            if (valueBitmapMemCache != null && )



            if (valueBitmapMemCache == null || (!valueBitmapMemCache.isMaxSize() && (width * height > ImageCache.DEFAULT_MAX_SIZE))) {
                valueBitmapMemCache = getBitmapFromCache(mMemoryCacheLarge, url);
            }

            if (valueBitmapMemCache != null) {
                // Nếu valueBitmapMemCache đã lưu trong memcache chưa phải là valueBitmapMemCache có size lớn nhất
                if (!valueBitmapMemCache.isMaxSize() && (valueBitmapMemCache.getWidth() * valueBitmapMemCache.getHeight() < width * height || width * height == -1)) {
                    valueBitmapMemCache = null;
                }
            }
        }
    }

    private ValueBitmapMemCache getBitmapFromCache(LruCache<String, ValueBitmapMemCache> cache, String key) {
        if (cache != null && !TextUtils.isEmpty(key)) {
            return cache.get(key);
        }

        return null;
    }

    // Memory Cache
    private void addBitmapToMemoryCache(String key, ValueBitmapMemCache bitmap) { //?
        if (!isBitmapFromMemoryCache(key) && bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    private boolean isBitmapFromMemoryCache(String key) { //?
        return mMemoryCache.snapshot().containsKey(key);
    }
    //*

    //Memory Large Cache
    private void addBitmapToMemoryLargeCache(String key, ValueBitmapMemCache bitmap) { //?
        if (!isBitmapFromMemoryLargeCache(key) && bitmap != null) {
            mMemoryCacheLarge.put(key, bitmap);
        }
    }

    private boolean isBitmapFromMemoryLargeCache(String key) { //?
        return mMemoryCacheLarge.snapshot().containsKey(key);
    }
    //*

    // Disk Cache
    public void addBitmapToDiskCache(String key, byte[] bytes) {
        synchronized (mDiskCacheLock) {//?
            if (mDiskCacheSimple != null && mDiskCacheSimple.get(key) == null) {
                mDiskCacheSimple.put(key, bytes);
            }
        }
    }

    public boolean isBitmapFromDiskCache(String key) { //?
        return mDiskCacheSimple.isBitmapFromDisk(key);
    }

    public Bitmap getBitmapFromDiskCache(String key) {
        synchronized (mDiskCacheLock) { //?
            return mDiskCacheSimple != null ? mDiskCacheSimple.get(key) : null;
        }
    }

    public Bitmap getBitmapFromDiskCache(String key, int width, int height, BitmapFactory.Options options) {
        synchronized (mDiskCacheLock) { //?
            return mDiskCacheSimple != null ? mDiskCacheSimple.get(key, width, height, options) : null; //?
        }
    }

    //*


}
