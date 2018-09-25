package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.AddImageRunnable;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.DataDownload;
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
    private Handler mHandler;
    private String mUrl;

    public DownloadImageRunnable(String mUrl, Handler mHandler) {
        this.mUrl = mUrl;
        this.mHandler = mHandler;
    }


    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        byte[] bytes = downloadImage(mUrl);
        DataDownload data = new DataDownload(mUrl, bytes);
        Message message = mHandler.obtainMessage(IMAGE_DOWNLOAD_RESULT_CODE, data);
        message.sendToTarget();
    }

    private byte[] downloadImage(String imgUrl) {
        InputStream inputStream = null;
        HttpURLConnection connection = null;

        byte[] bytes = new byte[0];
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

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }
}
