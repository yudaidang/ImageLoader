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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

public class DownloadImageRunnable implements Runnable {

    public static final AtomicLong seq = new AtomicLong(0);
    private final int mSeqNumb;
    //    private final long seqNum; //? ImageWorker.seqNum?
    private final ImageCache imageCache;
    private String imgUrl;
    private Handler mHandler;
    private int width;
    private int height;
    private NetworkCheck networkCheck; //? keep instance
    final BitmapFactory.Options options = new BitmapFactory.Options();

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
        Log.d("therad: ", Thread.currentThread().getName() + " ");

        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        if (!networkCheck.isOnline()) {
            return;
        }
        Bitmap bitmap;
        if (imageCache.getBitmapFromDiskCache(imgUrl) != null) {
            bitmap = imageCache.getBitmapFromDiskCache(imgUrl, width, height, options);
            imageCache.addBitmapToMemoryCache(imgUrl, bitmap);

        } else {
            bitmap = downloadImage(imgUrl);
            imageCache.addBitmapToMemoryCache(imgUrl, bitmap);
        }

        Message message = mHandler.obtainMessage(mSeqNumb, bitmap);
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

            new DiskCacheAsyntask(imgUrl).execute(bytes);

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
        while(((options.outHeight/2) / inSampleSize) >= heightReq && ((options.outWidth/2) / inSampleSize) >= widthReq){
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    protected class DiskCacheAsyntask extends AsyncTask<byte[], Void, Void> { //? AsyncTask ?
        private String imgUrlEx;

        public DiskCacheAsyntask(String imgUrl) {
            this.imgUrlEx = imgUrl;
        }

        @Override
        protected Void doInBackground(byte[]... bytes) {
            imageCache.addBitmapToDiskCache(imgUrlEx, bytes[0]);
            return null;
        }

    }
}
