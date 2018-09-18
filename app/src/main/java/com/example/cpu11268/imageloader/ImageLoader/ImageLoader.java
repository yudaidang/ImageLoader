package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.MessageBitmap;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by hungnq3 on 05/09/18.
 */
public class ImageLoader implements Handler.Callback {
    private static ImageLoader sInstance = new ImageLoader();
    private static Executor executorInternet;
    private final Handler mHandler;
    private Executor executor;

    private HashMap<String, Set<ImageWorker>> listImageWorker = new HashMap<>();
    private HashMap<Integer, ImageWorker> listViewCallback
            = new HashMap<>();

    public ImageLoader() {

        mHandler = new Handler(this);
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

    public void loadImageWorker(Context context, ImageWorker imageWorker) {
        if (DiskCacheSimple.getInstance().getDiskCacheDir() == null) {
            File diskCacheDir = getDiskCacheDir(context.getApplicationContext(), "IMAGE");
            if (!diskCacheDir.exists()) {
                diskCacheDir.mkdirs();
            }
            DiskCacheSimple.getInstance().setListFile(diskCacheDir);
        }

        Bitmap bitmap;

        if (imageWorker.mUrl == null) {
            imageWorker.onDownloadComplete(null);
        }
        if (imageWorker.mView.get() != null) {
            ImageWorker im = listViewCallback.get(imageWorker.mView.get().hashCode());
            for (Set<ImageWorker> listTemp : listImageWorker.values()) {
                if (listTemp.contains(im)) {
                    listTemp.remove(im);
                    listViewCallback.remove(imageWorker.mView.get().hashCode());
                }
            }
            listViewCallback.put(imageWorker.mView.get().hashCode(), imageWorker);
        } else {
            listViewCallback.put(imageWorker.mCallback.hashCode(), imageWorker);
        }

        bitmap = ImageCache.getInstance().findBitmapCache(imageWorker.mUrl, imageWorker.mWidth, imageWorker.mHeight);
        if (bitmap == null) {
            Set<ImageWorker> list;

            if (listImageWorker.containsKey(imageWorker.mUrl)) {
                list = listImageWorker.get(imageWorker.mUrl);
            } else {
                list = new HashSet<>();
                DiskBitmapRunnable diskBitmapRunnable = new DiskBitmapRunnable(executorInternet, context,
                        imageWorker.mUrl, mHandler, imageWorker.mWidth, imageWorker.mHeight);
                executor.execute(diskBitmapRunnable);
            }

            list.add(imageWorker);
            listImageWorker.put(imageWorker.mUrl, list);
        } else {
            imageWorker.onDownloadComplete(bitmap, imageWorker.mCallback);
        }
    }

    public void clearCallback(ImageWorker.MyDownloadCallback callback) {
        if (callback != null) {
            ImageWorker im = listViewCallback.get(callback.hashCode());
            for (Set<ImageWorker> listTemp : listImageWorker.values()) {
                if (listTemp.contains(im)) {
                    listTemp.remove(im);
                    listViewCallback.remove(callback.hashCode());
                }
            }
        }
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

        if (msg.what == DownloadImageRunnable.IMAGE_DOWNLOAD_RESULT_CODE || msg.what == DiskBitmapRunnable.IMAGE_LOADED_FROM_DISK_RESULT_CODE) {
            MessageBitmap messageBitmap = (MessageBitmap) msg.obj;
            if (listImageWorker.containsKey(messageBitmap.getmUrl())) {
                Set<ImageWorker> list = listImageWorker.get(messageBitmap.getmUrl());
                if (list != null) {
                    for (ImageWorker im : list) {
                        if (listViewCallback.containsKey(im.mView.get())) {
                            listViewCallback.remove(im.mView.get());
                            ((ImageView) im.mView.get()).setImageBitmap(null);
                        }
                        im.onDownloadComplete(messageBitmap.getmBitmap(), im.mCallback);
                    }
                }
                listImageWorker.remove(messageBitmap.getmUrl());
            }
        }
        return true;
    }
}
