package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.ObjectArrayView;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hungnq3 on 05/09/18.
 */
public class ImageLoader implements Handler.Callback{

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
                    addLast(o);
                    return true;
                }

                @Override
                public void put(Object o) throws InterruptedException {
                    if (contains(o)) {
                        remove(o);
                    }
                    super.putLast(o);
                }

                @Override
                public boolean offer(Object o) {
                    return offerLast(o);
                }

                @Override
                public Object poll() {
                    return pollLast();
                }

                @Override
                public Object poll(long timeout, TimeUnit unit) throws InterruptedException {
                    return super.pollLast(timeout, unit);
                }

                @Override
                public Object take() throws InterruptedException {
                    return takeLast();
                }

                @Override
                public Object peek() {
                    return super.peekLast();
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
                    addLast(o);
                    return true;
                }

                @Override
                public void put(Object o) throws InterruptedException {
                    if (contains(o)) {
                        remove(o);
                    }
                    super.putLast(o);
                }

                @Override
                public boolean offer(Object o) {
                    return offerLast(o);
                }

                @Override
                public Object poll() {
                    return pollLast();
                }

                @Override
                public Object poll(long timeout, TimeUnit unit) throws InterruptedException {
                    return super.pollLast(timeout, unit);
                }

                @Override
                public Object take() throws InterruptedException {
                    return takeLast();
                }

                @Override
                public Object peek() {
                    return super.peekLast();
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
//        loadImageWorker(context, imageWorker);
    }

    public void load(Context context, String mUrl, ImageWorker.MyDownloadCallback callback, int width, int height) {
        ImageWorker imageWorker = new ImageWorker(executor, executorInternet, mListCallbackKey, mListViewPlus, mListView);
        imageWorker.setInfoImageWorker(width, height);
        imageWorker.setInfoImageWorker(mUrl, callback);
//        loadImageWorker(context, imageWorker);

    }

    public void load(Context context, String mUrl, ImageView imageView, int width, int height) {
        ImageWorker imageWorker = new ImageWorker(executor, executorInternet, mListCallbackKey, mListViewPlus, mListView);
        imageWorker.setInfoImageWorker(width, height);
        imageWorker.setInfoImageWorker(mUrl, imageView);
//        loadImageWorker(context, imageWorker);

    }

    public void load(Context context, String url, ImageView imageView, String id) {
        ImageWorker imageWorker = new ImageWorker(executor, executorInternet, mListCallbackKey, mListViewPlus, mListView);
        imageWorker.setInfoImageWorker(url, imageView);
        loadImageWorker(context, imageWorker, id);

    }

    private void loadImageWorker(Context context, ImageWorker imageWorker, String id) {
        if (DiskCacheSimple.getInstance().getDiskCacheDir() == null) {
            File diskCacheDir = getDiskCacheDir(context.getApplicationContext(), "IMAGE");
            if (!diskCacheDir.exists()) {
                diskCacheDir.mkdirs();
            }
            DiskCacheSimple.getInstance().setListFile(diskCacheDir);
        }

        imageWorker.loadImage(context, id);
    }

    public void clearView(View view) {
        if (mListView.containsKey(view)) {
            ImageWorker.MyDownloadCallback callback = mListView.get(view);
            if (callback != null) {
                ObjectArrayView object = mListCallbackKey.get(callback);
                if (object != null && mListViewPlus.containsKey(object.getmUrl())) {
                    mListViewPlus.get(object.getmUrl()).remove(callback);
                }
                mListCallbackKey.remove(callback);
            }
            mListView.remove(view);
        }
    }

    public void clearCallback(ImageWorker.MyDownloadCallback callback) {
        if (mListCallbackKey.containsKey(callback)) {
            ObjectArrayView object = mListCallbackKey.get(callback);
            if (object != null) {
                View view = object.getView();
                String mUrl = object.getmUrl();
                if (view != null && mListView.containsKey(view)) {
                    mListView.remove(view);
                }
                if (!mUrl.isEmpty()) {
                    mListViewPlus.get(mUrl).remove(callback);
                }
            }
            mListCallbackKey.remove(callback);
        }
    }

    public void sameDownloadView(View view) {
        clearView(view);

    }

    private File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ?
                        Objects.requireNonNull(context.getExternalCacheDir()).getPath() :
                        context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }


    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
}
