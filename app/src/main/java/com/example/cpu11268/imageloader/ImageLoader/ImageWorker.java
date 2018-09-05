package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.CallBackImageView;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.InfoImageView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageWorker implements Handler.Callback {//generic
    public static final int DEFAULT_SIZE_SAMPLE = -1;
    private static Executor executor;
    private static AtomicInteger seq = new AtomicInteger(0);
    private static HashMap<String,ImageWorker> mListView = new HashMap<>();
    private static HashMap<View, String> mListView = new HashMap<>();

    private final Handler mHandler;
    private String mUrlTemp;
    private MyDownloadCallback mCallback;

    private static ImageWorker sInstance = new ImageWorker();

    public static ImageWorker getInstance() {
        return sInstance;
    }

    public ImageWorker() { //?
        mHandler = new Handler(this);

        if (executor == null) {
            BlockingQueue queue = new LinkedBlockingDeque() {

                @Override
                public boolean add(Object o) {
                    if (contains(o)) {
                        remove(o);
                    }
                    addFirst(o);
                    return true;
                }

                @Override
                public void put(Object o) throws InterruptedException {
                    if (contains(o)) {
                        remove(o);
                    }
                    super.putFirst(o);
                }
            };
            executor = new ThreadPoolExecutor(
                    2,
                    3,
                    60L,
                    TimeUnit.SECONDS,

                    queue);
        }
    }

    public void setSizeSmallMemCache(int mMaxSize) {
        ImageCache.getInstance().setSizeSmallMem(mMaxSize);
    }

    public void setSizeLargeMemCache(int mMaxSize) {
        ImageCache.getInstance().setSizeLargeMem(mMaxSize);
    }

    private void onDownloadComplete(Bitmap bitmap, MyDownloadCallback myDownloadCallback) {
        if (myDownloadCallback != null) {
            myDownloadCallback.onLoad(bitmap, null, 0);
        }
    }

    //handle view bị recycle trong recyclerview
    public void clearView(View mView) {
        if (mListView.containsKey(this.mUrlTemp)) {
            ArrayList<InfoImageView> list = mListView.get(this.mUrlTemp);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getView() == mView) {
                    list.remove(i);
                    return;
                }
            }
        }
    }

    //handle call back bị recycle trong recyclerview
    public void clearCallback(MyDownloadCallback callback) {
        if (mListView.containsKey(this.mUrlTemp)) {
            ArrayList<InfoImageView> list = mListView.get(this.mUrlTemp);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getCallback() == callback) {
                    list.remove(i);
                    return;
                }
            }
        }
    }

    //handle cancel khi 1 view đang download image 1 thì download image 2
    public void cancelSameViewLoading(View mView) {
        for (String url : mListView.keySet()) {
            ArrayList<InfoImageView> list = mListView.get(url);

            for (int i = 0; i < list.size(); i++) {
                View v = list.get(i).getView();
                if (v == mView && list.size() == 1) {
                    mListView.remove(url);
                    return;
                } else if (v == mView) {
                    list.remove(i);
                    return;
                }

            }

        }

    }

    public void loadImage(Context context, String mUrl, MyDownloadCallback callback) {
        loadImage(context, mUrl, 0, 0, callback);
    }

    public void loadImage(Context context, String mUrl, ImageView mView) {
        loadImage(context, mUrl, 0, 0, new CallBackImageView(mView));
    }

    /*
        View: width, height
        MyDownloadCallback: default is ImageView(CallBackImageView)
        ValueBitmapMemCache:
     */

    public void loadImage(Context context, final String mUrl, int width, int height, MyDownloadCallback callback) {
        this.mUrlTemp = mUrl;

        ValueBitmapMemCache valueBitmapMemCache;

        if (mUrl == null) {
            onDownloadComplete(null, callback);
            return;
        }

        valueBitmapMemCache = ImageCache.getInstance().findBitmapCache(mUrl, width, height)

        if (valueBitmapMemCache == null) {
            ArrayList<MyDownloadCallback> list;
            //Kiểm tra url có đang được download ko?
            if (mListView.containsKey(mUrl)) { //?
                list = mListView.get(mUrl);
            } else {
                list = new ArrayList<>();

                DiskBitmapRunnable diskBitmapRunnable = new DiskBitmapRunnable(context, mUrl, mHandler, width, height);
                executor.execute(diskBitmapRunnable);

            }
            list.add(callback);
            //add callback, view vào list đang được download với cùng url.
            mListView.put(mUrl, list);
        } else {
            onDownloadComplete(valueBitmapMemCache.getBitmap(), callback);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == DownloadImageRunnable.IMAGE_DOWNLOAD_RESULT_CODE || msg.what == DiskBitmapRunnable.IMAGE_LOADED_FROM_DISK_RESULT_CODE) {

            Pair<String, Bitmap> pair = (Pair<String, Bitmap>) msg.obj;

            String url = pair.first;
            Bitmap bitmap = pair.second;

            ImageCache.getInstance().addBitmapToMemoryCacheTotal(url, new ValueBitmapMemCache(bitmap, width, height, mMaxSize)); //?

            //kiểm tra list đang được download có chứa url này không?
            if (mListView.containsKey(url)) {
                ArrayList<InfoImageView> list = mListView.get(url);
                //return bitmap cho các callback với cùng url.
                for (int i = 0; i < list.size(); i++) {
                    onDownloadComplete(bitmap, list.get(i).getCallback());
                }
                mListView.remove(url);

            }
            return true;
        }

        return false;
    }

    public interface MyDownloadCallback {
        void onLoad(Bitmap bitmap, Object which, int resultCode);
    }
}
