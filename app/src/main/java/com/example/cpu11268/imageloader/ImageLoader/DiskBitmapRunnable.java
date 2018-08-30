package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.ValueBitmapMemCache;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkCheck;

import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DiskBitmapRunnable implements Runnable {


    private static Executor executorInternet;


    final BitmapFactory.Options options = new BitmapFactory.Options();
    private final int mSeqNumb;
    private final ImageCache imageCache;
    private String imgUrl;
    private Handler mHandler;
    private int width;
    private int height;
    private NetworkCheck networkCheck;//? keep instance: NOT


    public DiskBitmapRunnable(String imgUrl, Handler mHandler, int mSeqNumb, ImageCache imageCache, int width, int height, NetworkCheck networkCheck) {
        this.mSeqNumb = mSeqNumb;
        this.imgUrl = imgUrl;
        this.mHandler = mHandler;
        this.imageCache = imageCache;
        this.width = width;
        this.height = height;
        this.networkCheck = networkCheck;

        if (executorInternet == null) {
            PriorityBlockingQueue priorityBlockingQueue = new PriorityBlockingQueue<Runnable>(1
                    , new Comparator<Runnable>() {
                @Override
                public int compare(Runnable o1, Runnable o2) {
                    int result = 0;

                    try {
                        DownloadImageRunnable m1 = (DownloadImageRunnable) o1;
                        DownloadImageRunnable m2 = (DownloadImageRunnable) o2;

                        result = m1.getSeqNum() > m2.getSeqNum() ? -1 : 1;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return result;
                }
            });
            executorInternet = new ThreadPoolExecutor(
                    2,
                    3,
                    60L,
                    TimeUnit.SECONDS,

                    priorityBlockingQueue);
        }
    }

    public long getSeqNum() {
        return mSeqNumb;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        Bitmap bitmap;


        if (imageCache.isBitmapFromDiskCache(imgUrl)) {
            if (width == ImageWorker.DEFAULT_SIZE_SAMPLE || height == ImageWorker.DEFAULT_SIZE_SAMPLE) {
                bitmap = imageCache.getBitmapFromDiskCache(imgUrl);
            } else {
                bitmap = imageCache.getBitmapFromDiskCache(imgUrl, width, height, options);

            }

            Log.d("bitmapfromdisk ", bitmap.getByteCount() + "");
            imageCache.addBitmapToMemoryCacheTotal(imgUrl, new ValueBitmapMemCache(bitmap, width, height));
            Message message = mHandler.obtainMessage(imgUrl.hashCode(), bitmap);
            message.sendToTarget();
        } else {

            DownloadImageRunnable downloadImageRunnable = new DownloadImageRunnable(imgUrl, mHandler, mSeqNumb, imageCache, width, height, networkCheck);
            executorInternet.execute(downloadImageRunnable);
        }

    }

}
