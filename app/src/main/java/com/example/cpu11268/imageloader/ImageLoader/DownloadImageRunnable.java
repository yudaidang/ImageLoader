package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.util.Pair;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.AddImageRunnable;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkChecker;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadImageRunnable implements Runnable {

    public static final int IMAGE_DOWNLOAD_RESULT_CODE = 100;
    private static Executor mExecutor = new ThreadPoolExecutor(2,
            3, 60L,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private final BitmapFactory.Options options = new BitmapFactory.Options();
    private String imgUrl;
    private Handler mHandler;
    private int width;
    private int height;

    public DownloadImageRunnable(String imgUrl, Handler mHandler, int width, int height) {
        this.imgUrl = imgUrl;
        this.mHandler = mHandler;
        this.width = width;
        this.height = height;
    }

    @Override
    public void run() {

        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        try {
            Thread.sleep(2000); //?
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bitmap bitmap = downloadImage(imgUrl);
        Message message = mHandler.obtainMessage(IMAGE_DOWNLOAD_RESULT_CODE, new Pair<String, Bitmap>(imgUrl, bitmap));
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

            AddImageRunnable addImageRunnable = new AddImageRunnable(imgUrl, bytes);
            mExecutor.execute(addImageRunnable);

            //*****
            if (width != ImageWorker.DEFAULT_SIZE_SAMPLE || height != ImageWorker.DEFAULT_SIZE_SAMPLE) {
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                options.inSampleSize = caculateInSampleSize(options, width, height);
                options.inMutable = true;
                options.inJustDecodeBounds = false;
            }
            int mTempWidth = options.outWidth;
            int mTempHeight = options.outHeight;
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            Log.d("YUHUHUHU ", bitmap.getByteCount() + "");

            if (mTempWidth == bitmap.getWidth() && mTempHeight == bitmap.getHeight()) {
                this.mMaxSize = true;
            }
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
