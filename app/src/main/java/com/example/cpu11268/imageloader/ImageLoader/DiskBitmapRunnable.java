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
//    private ImageKey imageKey;
    private ImageWorker imageWorker;
    private HashMap<String, HashSet<ImageWorker>> listDownloading = new HashMap<>();


    public DiskBitmapRunnable(Context context, ImageWorker imageWorker) {
        this.mContext = new WeakReference<>(context);
        this.imageWorker = imageWorker;
        mHandlerDownload = new Handler(this);
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        Bitmap bitmap;

        boolean mMaxSize = false;
        if (ImageCache.getInstance().isBitmapFromDiskCache(imageWorker.imageKey.getmUrl())) {
            if (imageWorker.imageKey.getSize() == ImageWorker.DEFAULT_MAX_SIZE) {
                bitmap = ImageCache.getInstance().getBitmapFromDiskCache(imageWorker.imageKey.getmUrl());
                mMaxSize = true;
            } else {
                bitmap = ImageCache.getInstance().getBitmapFromDiskCache(imageWorker.imageKey.getmUrl(), imageWorker.imageKey.getSize(), imageWorker.imageKey.getSize(), options);
            }
            ImageCache.getInstance().addBitmapToMemoryCacheTotal(imageWorker.imageKey, new ValueBitmapMemCache(bitmap, mMaxSize)); //?
            handleResult(imageWorker.imageKey, bitmap);
        } else {
            if (mContext.get() != null && NetworkChecker.isOnline(mContext.get())) {
                HashSet<ImageWorker> list;
                if (!listDownloading.containsKey(imageWorker.imageKey.getmUrl())) {
                    list = new HashSet<>();
                    DownloadImageRunnable downloadImageRunnable = new DownloadImageRunnable(imageWorker.imageKey.getmUrl(), mHandlerDownload);
                    ImageLoader.getInstance().executorInternet.execute(downloadImageRunnable);

                } else {
                    list = listDownloading.get(imageWorker.imageKey.getmUrl());
                }
                list.add(imageWorker);
                listDownloading.put(imageWorker.imageKey.getmUrl(), list);

            } else {
                handleResult(imageWorker.imageKey, null);
            }
        }
    }

    private void handleResult(ImageKey imageKey, Bitmap bitmap) {
        MessageBitmap messageBitmap = new MessageBitmap(imageKey, bitmap, false);
        Message message = ImageLoader.getInstance().mHandler.obtainMessage(IMAGE_LOADED_FROM_DISK_RESULT_CODE, messageBitmap);
        message.sendToTarget();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == DownloadImageRunnable.IMAGE_DOWNLOAD_RESULT_CODE) {
            DataDownload data = (DataDownload) msg.obj;
            if (listDownloading.containsKey(data.getmUrl())) {
                Set<ImageWorker> list = listDownloading.get(data.getmUrl());
                if (list != null) {
                    for (ImageWorker ik : list) {
                        ik.setImageBitmap(data.getBytes(), options);
                    }
                }
                listDownloading.remove(data.getmUrl());
            }
        }
        return false;
    }


}
