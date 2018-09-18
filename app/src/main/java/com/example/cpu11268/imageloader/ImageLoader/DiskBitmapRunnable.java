package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.MessageBitmap;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkChecker;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

public class DiskBitmapRunnable implements Runnable {
    public static final int IMAGE_LOADED_FROM_DISK_RESULT_CODE = 101;


    private Executor executorInternet;
    private final WeakReference<Context> mContext;
    private final BitmapFactory.Options options = new BitmapFactory.Options();
    private String imgUrl;
    private Handler mHandler;
    private int width;
    private int height;

    public DiskBitmapRunnable(Executor executorInternet, Context context, String imgUrl, Handler mHandler, int width, int height) {
        this.imgUrl = imgUrl;
        this.mHandler = mHandler;
        this.width = width;
        this.height = height;
        this.mContext = new WeakReference<>(context);
        this.executorInternet = executorInternet;

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
            }
            if (mMaxSize && width == 0 && height == 0) {
                width = bitmap.getWidth();
                height = bitmap.getHeight();
            }
            ImageCache.getInstance().addBitmapToMemoryCacheTotal(new ImageKey(imgUrl, width, height), new ValueBitmapMemCache(bitmap, mMaxSize)); //?
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

    private void handleResult(String url, Bitmap bitmap) {
        MessageBitmap messageBitmap = new MessageBitmap(url, bitmap, false);
        Message message = mHandler.obtainMessage(IMAGE_LOADED_FROM_DISK_RESULT_CODE, messageBitmap);
        message.sendToTarget();
    }

}
