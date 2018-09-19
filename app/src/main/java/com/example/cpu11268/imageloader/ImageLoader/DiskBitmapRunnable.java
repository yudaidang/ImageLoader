package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.MessageBitmap;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkChecker;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

public class DiskBitmapRunnable implements Runnable {
    public static final int IMAGE_LOADED_FROM_DISK_RESULT_CODE = 101;


    private Executor executorInternet;
    private final WeakReference<Context> mContext;
    private final BitmapFactory.Options options = new BitmapFactory.Options();
    private Handler mHandler;
    private ImageKey imageKey;

    public DiskBitmapRunnable(Executor executorInternet, Context context, ImageKey imageKey, Handler mHandler) {
        this.mHandler = mHandler;
        this.mContext = new WeakReference<>(context);
        this.executorInternet = executorInternet;
        this.imageKey = imageKey;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        Bitmap bitmap;

        boolean mMaxSize = false;
        if (ImageCache.getInstance().isBitmapFromDiskCache(imageKey.getmUrl())) {
            if (imageKey.getSize() == ImageWorker.DEFAULT_SIZE_SAMPLE) {
                bitmap = ImageCache.getInstance().getBitmapFromDiskCache(imageKey.getmUrl());
                mMaxSize = true;
            } else {
                bitmap = ImageCache.getInstance().getBitmapFromDiskCache(imageKey.getmUrl(), imageKey.getSize(), imageKey.getSize(), options);
            }
            ImageCache.getInstance().addBitmapToMemoryCacheTotal(imageKey, new ValueBitmapMemCache(bitmap, mMaxSize)); //?
            handleResult(imageKey, bitmap);
        } else {
            if (mContext.get() != null && NetworkChecker.isOnline(mContext.get())) {
                DownloadImageRunnable downloadImageRunnable = new DownloadImageRunnable(imageKey, mHandler);
                executorInternet.execute(downloadImageRunnable);
            } else {
                handleResult(imageKey, null);
            }
        }
    }

    private void handleResult(ImageKey imageKey, Bitmap bitmap) {
        MessageBitmap messageBitmap = new MessageBitmap(imageKey, bitmap, false);
        Message message = mHandler.obtainMessage(IMAGE_LOADED_FROM_DISK_RESULT_CODE, messageBitmap);
        message.sendToTarget();
    }

}
