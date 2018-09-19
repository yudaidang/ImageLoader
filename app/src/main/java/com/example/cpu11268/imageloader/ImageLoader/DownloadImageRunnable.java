package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.AddImageRunnable;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.MessageBitmap;

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

    public static final int IMAGE_DOWNLOAD_RESULT_CODE = 100;
    private static Executor mExecutor = new ThreadPoolExecutor(2,
            3, 60L,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private final BitmapFactory.Options options = new BitmapFactory.Options();
    private Handler mHandler;
    private boolean mMaxSize;
    private ImageKey imageKey;

    public DownloadImageRunnable(ImageKey imageKey, Handler mHandler) {
        this.imageKey = imageKey;
        this.mHandler = mHandler;
    }

    @Override
    public void run() {

        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        Bitmap bitmap = downloadImage(imageKey.getmUrl());

        MessageBitmap messageBitmap = new MessageBitmap(imageKey, bitmap, mMaxSize);
        Message message = mHandler.obtainMessage(IMAGE_DOWNLOAD_RESULT_CODE, messageBitmap);
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

            if (imageKey.getSize() != ImageWorker.DEFAULT_SIZE_SAMPLE) {
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                options.inSampleSize = caculateInSampleSize(options, imageKey.getSize(), imageKey.getSize());
                options.inMutable = true;
                options.inJustDecodeBounds = false;
            }
            int mTempWidth = options.outWidth;
            int mTempHeight = options.outHeight;



            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
            int msize = imageKey.getSize();
            if (mTempWidth == bitmap.getWidth() && mTempHeight == bitmap.getHeight()) {
                this.mMaxSize = true;
                msize = 0;
            }

            ImageCache.getInstance().addBitmapToMemoryCacheTotal(new ImageKey(imageKey.getmUrl(), msize, msize), new ValueBitmapMemCache(bitmap, mMaxSize)); //?

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
