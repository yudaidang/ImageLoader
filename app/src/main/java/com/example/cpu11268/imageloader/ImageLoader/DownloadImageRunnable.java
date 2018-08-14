package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkCheck;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

public class DownloadImageRunnable implements Runnable {

    public static final AtomicLong seq = new AtomicLong(0);
    private final int mId;
    private final long seqNum;
    private final ImageCache imageCache;
    private String imgUrl;
    private Handler mHandler;
    private int width;
    private int height;
    private NetworkCheck networkCheck;
    public DownloadImageRunnable(String imgUrl, Handler mHandler, int mId, ImageCache imageCache, int width, int height, NetworkCheck networkCheck) {
        this.mId = mId;
        this.imgUrl = imgUrl;
        this.mHandler = mHandler;
        seqNum = seq.getAndIncrement();
        this.imageCache = imageCache;
        this.width = width;
        this.height = height;
        this.networkCheck = networkCheck;
    }

    public long getSeqNum() {
        return seqNum;
    }


    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        if (!networkCheck.isOnline()) {
            return;
        }
        Bitmap bitmap;
        if (imageCache.getBitmapFromDiskCache(imgUrl) != null) {
            bitmap = imageCache.getBitmapFromDiskCache(imgUrl, width, height);
            imageCache.addBitmapToMemoryCache(imgUrl, bitmap);

        } else {
            bitmap = downloadImage(imgUrl);
            imageCache.addBitmapToMemoryCache(imgUrl, bitmap);
        }

        Message message = mHandler.obtainMessage(mId, bitmap);
        message.sendToTarget();
    }

    protected class DiskCacheAsyntask extends AsyncTask<byte[], Void, Void>{
        private String imgUrlEx;

        public DiskCacheAsyntask(String imgUrl) {
            this.imgUrlEx = imgUrl;
        }

        @Override
        protected Void doInBackground(byte[]... bytes) {
            imageCache.addBitmapToDiskCache(imgUrlEx, bytes[0], width, height);
            return null;
        }

    }

    private Bitmap downloadImage(String imgUrl) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        byte[] bytes = null;
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

            new DiskCacheAsyntask(imgUrl).execute(bytes);

            //*****
            BitmapFactory.Options options = new BitmapFactory.Options();
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.disconnect();

        }

        return bitmap;
    }

    private int caculateInSampleSize(BitmapFactory.Options options, int widthReq, int heightReq) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > heightReq || width > widthReq) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= heightReq && (halfWidth / inSampleSize) >= widthReq) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
