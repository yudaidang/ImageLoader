package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.BitmapPolicy;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.Entry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DiskCacheSimple {
    private static final int DEFAULT_MAX_SIZE = 1024 * 1024 * 30;
    private static DiskCacheSimple sInstance = new DiskCacheSimple();
    private BitmapPolicy mBitmapPolicy;
    private LinkedHashMap mFilesInCache;
    private long mCurrentSize;
    private int mMaxSize = DEFAULT_MAX_SIZE;
    private File diskCacheDir = null;

    private DiskCacheSimple() {
        mBitmapPolicy = new BitmapPolicy();
    }

    public static DiskCacheSimple getInstance() {
        return sInstance;
    }

    public File getDiskCacheDir() {
        return diskCacheDir;
    }

    public void setListFile(File f) {
        this.diskCacheDir = f;
        mFilesInCache = new LinkedHashMap<>(16, 0.75f, true);
        File files[] = f.listFiles();
        List allFiles = new ArrayList<Entry>();
        for (File fl : files) {
            if (!fl.isDirectory()) {
                long length = fl.length();
                String hashedValue = fl.getName();
                Entry entry = new Entry(fl, length, Integer.parseInt(hashedValue));
                allFiles.add(entry);
            }
        }

        Collections.sort(allFiles, new Comparator<Entry>() {

            @Override
            public int compare(Entry o1, Entry o2) {
                long diff = o1.file.lastModified() - o2.file.lastModified();
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

    private void addEntryToHash(Entry entry) {
        mFilesInCache.put(entry.key, entry);
        mCurrentSize += entry.sizeBytes;
    }

    public void setMaxSize(int size) {
        this.mMaxSize = size;
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

    private synchronized void removeFromHash(Entry entry) {
        entry.file.delete();
        mFilesInCache.remove(entry.key);
        mCurrentSize -= entry.sizeBytes;
    }

    public boolean isExistFile(String key) {
        File file = new File(diskCacheDir, key.hashCode() + "");
        return file.exists() && file.length() != 0;
//        return mFilesInCache.containsKey(key.hashCode());

    }

    public synchronized Bitmap get(String key) {
        Entry cachedData = (Entry) mFilesInCache.get(key.hashCode());
        return cachedData != null ? mBitmapPolicy.read(cachedData.file) : null;
    }

    public synchronized Bitmap get(String key, int width, int height, BitmapFactory.Options options) {

        Entry cachedData = (Entry) mFilesInCache.get(key.hashCode());
        return cachedData != null ? mBitmapPolicy.read(cachedData.file, width, height, options) : null;
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
        if (value.length != 0) {
            try {
                mBitmapPolicy.write(new File(diskCacheDir, Integer.toString(hash)), value);
            } catch (IOException ex) {
                return false;
            }
            cachedData = new Entry(new File(diskCacheDir, Integer.toString(hash)), mBitmapPolicy.size(value), key.hashCode());

            addEntryToHash(cachedData);
        }
        return true;
    }


}







