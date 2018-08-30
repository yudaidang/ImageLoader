package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.CallBackImageView;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.InfoImageView;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkCheck;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.ValueBitmapMemCache;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageWorker implements Handler.Callback {//generic
    public static final int DEFAULT_SIZE_SAMPLE = -1;
    private static Executor executor;
    private static ImageCache imageCache = null;
    private static AtomicInteger seq = new AtomicInteger(0);
    private static HashMap<Integer, ArrayList<InfoImageView>> mListView = new HashMap<>();
    private final Handler mHandler;
    private MyDownloadCallback callback;
    private int seqNumber;
    private WeakReference<Context> context;
    private WeakReference<View> view;
    private NetworkCheck networkCheck;
    private String mUrlTemp;
    private int mWidth = DEFAULT_SIZE_SAMPLE;
    private int mHeight = DEFAULT_SIZE_SAMPLE;

    public ImageWorker(Context context) {
        mHandler = new Handler(this);
        this.context = new WeakReference<>(context);
        networkCheck = NetworkCheck.getInstance(context);
        if (imageCache == null) {
            imageCache = ImageCache.getInstance(context);
        }


        if (executor == null) {
            PriorityBlockingQueue priorityBlockingQueue = new PriorityBlockingQueue<Runnable>(1
                    , new Comparator<Runnable>() {
                @Override
                public int compare(Runnable o1, Runnable o2) {
                    int result = 0;

                    try {
                        DiskBitmapRunnable m1 = (DiskBitmapRunnable) o1;
                        DiskBitmapRunnable m2 = (DiskBitmapRunnable) o2;

                        result = m1.getSeqNum() > m2.getSeqNum() ? -1 : 1;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return result;
                }
            });
            executor = new ThreadPoolExecutor(
                    2,
                    3,
                    60L,
                    TimeUnit.SECONDS,

                    priorityBlockingQueue);
        }


    }

    public void onDownloadComplete(Bitmap bitmap, MyDownloadCallback callback) {
        if (callback != null) {
            callback.onLoad(bitmap);
        }
        callback = null;
    }

    public void clearView(View mView) {
        if (mListView.containsKey(this.mUrlTemp.hashCode())) {
            ArrayList<InfoImageView> list = mListView.get(this.mUrlTemp.hashCode());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getView() == mView) {
                    list.remove(i);
                    return;
                }
            }
        }
    }

    public void clearCallback(MyDownloadCallback callback) {
        if (mListView.containsKey(this.mUrlTemp.hashCode())) {
            ArrayList<InfoImageView> list = mListView.get(this.mUrlTemp.hashCode());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getCallback() == callback) {
                    list.remove(i);
                    return;
                }
            }
        }
    }

    public void cancelSameViewLoading(View mView) {
        for (Integer mUrlHashCode : mListView.keySet()) {
            ArrayList<InfoImageView> list = mListView.get(mUrlHashCode);
            Log.d("YUDAIDNG", mUrlHashCode + " " + list.size());

            for (int i = 0; i < list.size(); i++) {
                View v = list.get(i).getView();
                if (v == mView && list.size() == 1) {
                    mListView.remove(mUrlHashCode);
                    return;
                } else if (v == mView) {
                    list.remove(i);
                    return;
                }

            }

        }

    }

    public void loadImage(final String mUrl, MyDownloadCallback callback) {
        loadImage(mUrl, null, callback);
    }

    public void loadImage(final String mUrl, ImageView mView) {
        this.view = new WeakReference<>((View) mView);
        this.callback = new CallBackImageView(mView);
        this.mWidth = (int) (view.get().getLayoutParams().width / (Resources.getSystem().getDisplayMetrics().density));
        this.mHeight = (int) (view.get().getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));
        loadImage(mUrl, mView, callback);
    }

    public void setSampleSize(int mWidth, int mHeight) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    public void loadImage(final String mUrl, View mView, MyDownloadCallback callback) {
        this.callback = callback;
        this.view = new WeakReference<>(mView);
        this.mUrlTemp = mUrl;
        ValueBitmapMemCache bitmap;
        int maxWidthHeight = mWidth > mHeight ? mWidth : mHeight;


        if (maxWidthHeight < ImageCache.DEFAULT_MAX_WIDTH_HEIGHT) {
            bitmap = imageCache.getBitmapFromMemoryCache(mUrl);

        } else {
            bitmap = imageCache.getBitmapFromMemoryLargeCache(mUrl);
        }
        if(bitmap != null){
            if(bitmap.maxWidthHeight() < maxWidthHeight){
                bitmap = null;
            }
        }
        if (mUrl == null || !networkCheck.isOnline()) {
            onDownloadComplete(null, callback);
            return;
        }

        if (bitmap == null) {
            Log.d("BITMAPCHECK ", " DISK");

            ArrayList<InfoImageView> list;
            if (mListView.containsKey(mUrl.hashCode())) {
                list = mListView.get(mUrl.hashCode());
            } else {
                list = new ArrayList<>();
                seqNumber = seq.getAndIncrement() >= Integer.MAX_VALUE ? 0 : seq.getAndIncrement();

                DiskBitmapRunnable diskBitmapRunnable = new DiskBitmapRunnable(mUrl, mHandler, seqNumber, imageCache, mWidth, mHeight, networkCheck);
                executor.execute(diskBitmapRunnable);

            }
            list.add(new InfoImageView(callback, mView));
            mListView.put(mUrl.hashCode(), list);
        } else {
/*            Log.d("TESTBITMAP1", imageCache.getBitmapFromMemoryCache(mUrl).getByteCount() + " " + mWidth + " " + mHeight);
            Bitmap bitmapMem = Bitmap.createScaledBitmap(imageCache.getBitmapFromMemoryCache(mUrl), mWidth/2, mHeight/2, true);
            Log.d("TESTBITMAP2", bitmapMem.getByteCount() + "");
            Log.d("BITMAP ", bitmap.getByteCount() + " " + mWidth + " " + mHeight);*/
            Log.d("BITMAPCHECK ", " MEM");

            onDownloadComplete(bitmap.getmBitmap(), callback);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        final int msTemp = msg.what;
        final Bitmap bitmap = (Bitmap) msg.obj;

        if (mListView.containsKey(msTemp)) {
            ArrayList<InfoImageView> list = mListView.get(msTemp);
            for (int i = 0; i < list.size(); i++) {
                onDownloadComplete(bitmap, list.get(i).getCallback());
            }
            list.clear();
            mListView.remove(msTemp);

        }
        return true;
    }

    public interface MyDownloadCallback {
        void onLoad(Bitmap bitmap);
    }
}
