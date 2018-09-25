package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.DataDownload;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.MessageBitmap;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkChecker;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

public class DiskBitmapRunnable implements Runnable, Handler.Callback {
    public static final int IMAGE_LOADED_FROM_DISK_RESULT_CODE = 101;
    private final WeakReference<Context> mContext;
    private final BitmapFactory.Options options = new BitmapFactory.Options();
    private final Handler mHandlerDownload;
    private Executor executorInternet;
    private Handler mHandler;
    private ImageKey imageKey;
    private HashMap<String, HashSet<ImageKey>> listDownloading = new HashMap<>();

    public DiskBitmapRunnable(Executor executorInternet, Context context, ImageKey imageKey, Handler mHandler) {
        this.mHandler = mHandler;
        this.mContext = new WeakReference<>(context);
        this.executorInternet = executorInternet;
        this.imageKey = imageKey;
        mHandlerDownload = new Handler(this);
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        Bitmap bitmap;

        boolean mMaxSize = false;
        if (ImageCache.getInstance().isBitmapFromDiskCache(imageKey.getmUrl())) {
            if (imageKey.getSize() == ImageWorker.DEFAULT_MAX_SIZE) {
                bitmap = ImageCache.getInstance().getBitmapFromDiskCache(imageKey.getmUrl());
                mMaxSize = true;
            } else {
                bitmap = ImageCache.getInstance().getBitmapFromDiskCache(imageKey.getmUrl(), imageKey.getSize(), imageKey.getSize(), options);
            }
            ImageCache.getInstance().addBitmapToMemoryCacheTotal(imageKey, new ValueBitmapMemCache(bitmap, mMaxSize)); //?
            handleResult(imageKey, bitmap);
        } else {
            if (mContext.get() != null && NetworkChecker.isOnline(mContext.get())) {
                HashSet<ImageKey> list;
                if (!listDownloading.containsKey(imageKey.getmUrl())) {
                    list = new HashSet<>();
                    DownloadImageRunnable downloadImageRunnable = new DownloadImageRunnable(imageKey.getmUrl(), mHandlerDownload);
                    executorInternet.execute(downloadImageRunnable);
                } else {
                    list = listDownloading.get(imageKey.getmUrl());
                }
                list.add(imageKey);
                listDownloading.put(imageKey.getmUrl(), list);

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

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == DownloadImageRunnable.IMAGE_DOWNLOAD_RESULT_CODE) {
            DataDownload data = (DataDownload) msg.obj;
            boolean mMaxSize = false;
            if (listDownloading.containsKey(data.getmUrl())) {
                Set<ImageKey> list = listDownloading.get(data.getmUrl());
                if (list != null) {
                    for (ImageKey ik : list) {
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeByteArray(data.getBytes(), 0, data.getBytes().length, options);
                        options.inSampleSize = caculateInSampleSize(options, ik.getSize(), ik.getSize());
                        int width = options.outWidth;
                        int height = options.outHeight;
                        options.inJustDecodeBounds = false;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data.getBytes(), 0, data.getBytes().length, options);
                        handleResult(ik, bitmap);
                        if (width == bitmap.getWidth() && height == bitmap.getHeight()) {
                            mMaxSize = true;
                        }
                        ImageCache.getInstance().addBitmapToMemoryCacheTotal(ik, new ValueBitmapMemCache(bitmap, mMaxSize)); //?
                    }
                }
                listDownloading.remove(data.getmUrl());
            }
        }
        return false;
    }

    private int caculateInSampleSize(BitmapFactory.Options options, int widthReq, int heightReq) {

        int inSampleSize = 1;
        while (((options.outHeight / 2) / inSampleSize) >= heightReq && ((options.outWidth / 2) / inSampleSize) >= widthReq) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }
}
