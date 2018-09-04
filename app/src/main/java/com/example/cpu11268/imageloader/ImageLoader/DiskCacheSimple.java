package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.BitmapPolicy;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.Entry;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DiskCacheSimple {
    private static final int DEFAULT_MAX_SIZE = 1024 * 1024 * 30;
    private final LinkedHashMap mFilesInCache;
    private BitmapPolicy mBitmapPolicy;
    private long mMaxSize;
    private long mCurrentSize;
    private File diskCacheDir;

    private DiskCacheSimple(Context context) {
        mBitmapPolicy = new BitmapPolicy();
        diskCacheDir = getDiskCacheDir(context, "IMAGE");
        mMaxSize = DEFAULT_MAX_SIZE;
        mCurrentSize = 0;
        diskCacheDir.mkdirs();

        File files[] = diskCacheDir.listFiles();
        mFilesInCache = new LinkedHashMap<>(16, 0.75f, true);
        List allFiles = new ArrayList<Entry>();

        for (File file : files) {
            if (!file.isDirectory()) {
                long length = file.length();
                String hashedValue = file.getName();
                Entry entry = new Entry(file, length, Integer.parseInt(hashedValue));
                allFiles.add(entry);
            }
        }

        Collections.sort(allFiles, new Comparator<Entry>() {
            @Override
            public int compare(Entry lhs, Entry rhs) {
                long diff = rhs.file.lastModified() - lhs.file.lastModified();
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return 0;
            }
        });
        for (Object entry : allFiles) {
            addEntryToHash((Entry) entry);
        }
    }

    public static DiskCacheSimple getInstance(Context context) {
        return new DiskCacheSimple(context);
    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        WeakReference<Context> mContext = new WeakReference<>(context);
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ?
                        Objects.requireNonNull(mContext.get().getExternalCacheDir()).getPath() :
                        context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }

    private boolean checkSizeCache(int newSizeItem) {
        if (newSizeItem > mMaxSize) {
            return false;
        }

        while (mCurrentSize + newSizeItem > mMaxSize && mFilesInCache.size() > 0) {
            Iterator it = mFilesInCache.values().iterator();
            Entry entry = (Entry) it.next();
            removeFromHash(entry);
        }
        return true;
    }

    private void addEntryToHash(Entry entry) {
        mFilesInCache.put(entry.key, entry);
        mCurrentSize += entry.sizeBytes;
    }

    public Bitmap get(String key) {
        Entry cachedData = (Entry) mFilesInCache.get(key.hashCode());
        return cachedData != null ? mBitmapPolicy.read(cachedData.file) : null;
    }

    public boolean isBitmapFromDisk(String key) {
        return mFilesInCache.containsKey(key.hashCode());
    }

    public Bitmap get(String key, int width, int height, BitmapFactory.Options options) {
        Entry cachedData = (Entry) mFilesInCache.get(key.hashCode());
        return cachedData != null ? mBitmapPolicy.read(cachedData.file, width, height, options) : null;
    }

    private void removeFromHash(Entry entry) {
        entry.file.delete();
        mFilesInCache.remove(entry.key);
        mCurrentSize -= entry.sizeBytes;
    }

    public synchronized boolean put(String key, byte[] value) {
        int hash = key.hashCode();
        Entry cachedData = (Entry) mFilesInCache.get(hash);


        if (!checkSizeCache(value.length)) {
            return false;
        }

        if (cachedData != null) {
            removeFromHash(cachedData);
        }

        try {
            mBitmapPolicy.write(new File(diskCacheDir, Integer.toString(hash)), value);
        } catch (IOException ex) {
            return false;
        }
        cachedData = new Entry(new File(diskCacheDir, Integer.toString(hash)), mBitmapPolicy.size(value), key.hashCode());
        addEntryToHash(cachedData);
        return true;
    }


    public synchronized void remove(String key) {
        Entry entry = (Entry) mFilesInCache.get(key.hashCode());
        if (entry != null) {
            removeFromHash(entry);
        }
    }

    public synchronized void clear() {
        Iterator it = mFilesInCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Entry entryData = (Entry) entry.getValue();
            entryData.file.delete();
        }
        mFilesInCache.clear();
        mCurrentSize = 0;
    }
}







