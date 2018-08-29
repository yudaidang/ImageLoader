package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.AddImageRunnable;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkCheck;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadImageRunnable implements Runnable {

    private static Executor mExecutor = new ThreadPoolExecutor(2,
            3, 60L,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    final BitmapFactory.Options options = new BitmapFactory.Options();
    private final int mSeqNumb;
    private final ImageCache imageCache;
    private String imgUrl;
    private Handler mHandler;
    private int width;
    private int height;
    private NetworkCheck networkCheck;//? keep instance: NOT

    public DownloadImageRunnable(String imgUrl, Handler mHandler, int mSeqNumb, ImageCache imageCache, int width, int height, NetworkCheck networkCheck) {
        this.mSeqNumb = mSeqNumb;
        this.imgUrl = imgUrl;
        this.mHandler = mHandler;
        this.imageCache = imageCache;
        this.width = width;
        this.height = height;
        this.networkCheck = networkCheck;
    }

    public long getSeqNum() {
        return mSeqNumb;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);


        Bitmap bitmap;
        if (imageCache.getBitmapFromDiskCache(imgUrl) != null) {
            bitmap = imageCache.getBitmapFromDiskCache(imgUrl, width, height, options);
            imageCache.addBitmapToMemoryCache(imgUrl, bitmap);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bitmap = downloadImage(imgUrl);
            imageCache.addBitmapToMemoryCache(imgUrl, bitmap);
        }
        Message message = mHandler.obtainMessage(imgUrl.hashCode(), bitmap);
        message.sendToTarget();
    }

    private Bitmap downloadImage(String imgUrl) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        HttpURLConnection connection = null;

        byte[] bytes;
        try {
            URL url = new URL(imgUrl);
            //connection
            connection = (HttpURLConnection) url.openConnection();
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER) {
                String newUrl = connection.getHeaderField("Location");
                connection = (HttpURLConnection) new URL(newUrl).openConnection();
            }
            inputStream = connection.getInputStream();

            bytes = IOUtils.toByteArray(inputStream);

            AddImageRunnable addImageRunnable = new AddImageRunnable(imageCache, imgUrl, bytes);
            mExecutor.execute(addImageRunnable);

            //*****
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            options.inSampleSize = caculateInSampleSize(options, width, height);
            options.inMutable = true;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            //*****

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }

    private int caculateInSampleSize(BitmapFactory.Options options, int widthReq, int heightReq) {
        int inSampleSize = 1;
        while (((options.outHeight / 2) / inSampleSize) >= heightReq && ((options.outWidth / 2) / inSampleSize) >= widthReq) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }
}
