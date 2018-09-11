package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.ObjectArrayView;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hungnq3 on 05/09/18.
 */
public class ImageLoader {

    private static ImageLoader sInstance = new ImageLoader();
    private static Executor executorInternet;
    private Executor executor;
    private HashMap<ImageWorker.MyDownloadCallback, ObjectArrayView> mListCallbackKey = new HashMap<>();
    private HashMap<String, HashMap<ImageWorker.MyDownloadCallback, View>> mListViewPlus = new HashMap<>();
    private HashMap<View, ImageWorker.MyDownloadCallback> mListView = new HashMap<>();

    public ImageLoader() {
        if (executor == null) {
            BlockingQueue queue = new LinkedBlockingDeque() {

                @Override
                public boolean add(Object o) {
                    if (contains(o)) {
                        remove(o);
                    }
                    addFirst(o);
                    return true;
                }

                @Override
                public void put(Object o) throws InterruptedException {
                    if (contains(o)) {
                        remove(o);
                    }
                    super.putFirst(o);
                }
            };
            executor = new ThreadPoolExecutor(
                    2,
                    3,
                    60L,
                    TimeUnit.SECONDS,

                    queue);
        }

        if (executorInternet == null) {
            BlockingQueue queue = new LinkedBlockingDeque() {

                @Override
                public boolean add(Object o) {
                    if (contains(o)) {
                        remove(o);
                    }
                    addFirst(o);
                    return true;
                }

                @Override
                public void put(Object o) throws InterruptedException {
                    if (contains(o)) {
                        remove(o);
                    }
                    super.putFirst(o);
                }
            };
            executorInternet = new ThreadPoolExecutor(
                    2,
                    3,
                    60L,
                    TimeUnit.SECONDS,

                    queue);
        }
    }

    public static ImageLoader getInstance() {
        return sInstance;
    }


    public void setSizeSmallMemCache(int mMaxSize) {
        ImageCache.getInstance().setSizeSmallMem(mMaxSize);
    }

    public void setSizeLargeMemCache(int mMaxSize) {
        ImageCache.getInstance().setSizeLargeMem(mMaxSize);
    }

    public void load(Context context, String url, ImageWorker.MyDownloadCallback callback) {
        ImageWorker imageWorker = new ImageWorker(executor, executorInternet, mListCallbackKey, mListViewPlus, mListView);
        imageWorker.setInfoImageWorker(url, callback);
        loadImageWorker(context, imageWorker);
    }

    public void load(Context context, String mUrl, ImageWorker.MyDownloadCallback callback, int width, int height) {
        ImageWorker imageWorker = new ImageWorker(executor, executorInternet, mListCallbackKey, mListViewPlus, mListView);
        imageWorker.setInfoImageWorker(width, height);
        imageWorker.setInfoImageWorker(mUrl, callback);
        loadImageWorker(context, imageWorker);

    }

    public void load(Context context, String mUrl, ImageView imageView, int width, int height) {
        ImageWorker imageWorker = new ImageWorker(executor, executorInternet, mListCallbackKey, mListViewPlus, mListView);
        imageWorker.setInfoImageWorker(width, height);
        imageWorker.setInfoImageWorker(mUrl, imageView);
        loadImageWorker(context, imageWorker);

    }

    public void load(Context context, String url, ImageView imageView) {
        ImageWorker imageWorker = new ImageWorker(executor, executorInternet, mListCallbackKey, mListViewPlus, mListView);
        imageWorker.setInfoImageWorker(url, imageView);
        loadImageWorker(context, imageWorker);

    }

    private void loadImageWorker(Context context, ImageWorker imageWorker) {
        imageWorker.loadImage(context);
    }


}
