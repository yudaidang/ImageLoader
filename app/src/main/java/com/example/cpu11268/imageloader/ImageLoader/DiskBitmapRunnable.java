package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.util.Pair;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkChecker;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DiskBitmapRunnable implements Runnable {
    public static final int IMAGE_LOADED_FROM_DISK_RESULT_CODE = 101;


    private static Executor executorInternet;


    private final BitmapFactory.Options options = new BitmapFactory.Options();
    private String imgUrl;
    private Handler mHandler;
    private int width;
    private int height;
    final WeakReference<Context> mContext;

    public DiskBitmapRunnable(Context context, String imgUrl, Handler mHandler, int width, int height) {
        this.imgUrl = imgUrl;
        this.mHandler = mHandler;
        this.width = width;
        this.height = height;
        mContext = new WeakReference<>(context);

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

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        Bitmap bitmap;

        boolean mMaxSize = false;

        if (ImageCache.getInstance().isBitmapFromDiskCache(imgUrl)) {
            if (width == ImageWorker.DEFAULT_SIZE_SAMPLE || height == ImageWorker.DEFAULT_SIZE_SAMPLE) {
                bitmap = ImageCache.getInstance().getBitmapFromDiskCache(imgUrl);
                mMaxSize = true;
            } else {
                bitmap = ImageCache.getInstance().getBitmapFromDiskCache(imgUrl, width, height, options);
                Log.d("YUHUHUHU ", bitmap.getByteCount() + "");
            }
            ImageCache.getInstance().addBitmapToMemoryCacheTotal(imgUrl, new ValueBitmapMemCache(bitmap, mMaxSize)); //?
            handleResult(imgUrl, bitmap);
        } else {
            if (mContext.get() != null && NetworkChecker.isOnline(mContext.get())) {
                DownloadImageRunnable downloadImageRunnable = new DownloadImageRunnable(imgUrl, mHandler, width, height);
                executorInternet.execute(downloadImageRunnable);
            } else {
                handleResult(imgUrl, null);
            }
        }

    }

    private void handleResult (String url, Bitmap bitmap) {
        Message message = mHandler.obtainMessage(IMAGE_LOADED_FROM_DISK_RESULT_CODE, new Pair<>(url, bitmap));
        message.sendToTarget();
    }

}
