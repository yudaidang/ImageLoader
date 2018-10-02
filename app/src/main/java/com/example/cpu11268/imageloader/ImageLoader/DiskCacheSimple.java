package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.BitmapPolicy;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class DiskCacheSimple {
    private static DiskCacheSimple sInstance = new DiskCacheSimple();
    private BitmapPolicy mBitmapPolicy;
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

    public void setDiskCacheDir(File diskCacheDir) {
        this.diskCacheDir = diskCacheDir;
    }

    public synchronized void clearDisk(int mMaxSize) {
        this.clearDisk(null, mMaxSize);
    }

    public synchronized void clearDisk(File mDiskDir, int mMaxSize) {
        if (mDiskDir == null) {
            mDiskDir = diskCacheDir;
        }

        List allFiles = new ArrayList();
        for (File f : mDiskDir.listFiles()) {
            allFiles.add(f);
        }

        Collections.sort(allFiles, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                long diff = o1.lastModified() - o2.lastModified();
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return 0;
            }
        });

        while (mDiskDir.length() > mMaxSize) {
            Iterator it = allFiles.iterator();
            File file = (File) it.next();
            allFiles.remove(file);
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public boolean isExistFile(String mPath) {
        File f = new File(mPath);
        return f.exists() && f.length() != 0 ? true : false;
    }

    public synchronized Bitmap get(String mPath) {
        return isExistFile(mPath) ? mBitmapPolicy.read(new File(mPath)) : null;
    }

    public synchronized Bitmap get(String mPath, int width, int height, BitmapFactory.Options options) {
        return isExistFile(mPath) ? mBitmapPolicy.read(new File(mPath), width, height, options) : null;
    }

    //editting
    public synchronized boolean put(String key, byte[] value, String diskPath) {
        if (value.length != 0) {
            File file = new File(diskPath);
            if(!file.exists()) {
                file.mkdirs();
            }

            try {
                mBitmapPolicy.write(new File(file, key.hashCode() + ""), value);
            } catch (IOException ex) {
                return false;
            }
        }
        return true;
    }


}
