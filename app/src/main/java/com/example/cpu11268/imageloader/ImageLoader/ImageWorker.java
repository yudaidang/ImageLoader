package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkCheck;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageWorker<T extends View> implements Handler.Callback {//generic
    private static Executor executor;
    private static ImageCache imageCache = null;
    private static AtomicInteger seq = new AtomicInteger(0);
    private static HashMap<Integer, ArrayList<MyDownloadCallback>> mListView = new HashMap<>();
    private final Handler mHandler;
    private MyDownloadCallback callback;
    private int seqNumber;
    private WeakReference<Context> context;
    private WeakReference<T> view;
    private NetworkCheck networkCheck;
    private String idTemp;

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
                        DownloadImageRunnable m1 = (DownloadImageRunnable) o1;
                        DownloadImageRunnable m2 = (DownloadImageRunnable) o2;

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


    public void setDownloadListener(MyDownloadCallback callback) {
        this.callback = callback;
    }

    public void onDownloadComplete(Bitmap bitmap, MyDownloadCallback callback) {
        if (callback != null) {
            callback.onLoad(bitmap);
        }
        callback = null;
    }

    public void clearCallback(T view){

    }

    public void loadImage(final String mUrl, final String id, T mView) {
        this.view = new WeakReference<>(mView);

        Log.d("YUDAIDANG", id + " " + mUrl);
        this.idTemp = id;
        if (mUrl == null) {
            onDownloadComplete(null, callback);
            return;
        }
        final int widthView = (int) (view.get().getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));
        final int heightView = (int) (view.get().getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));
        final Bitmap bitmap = imageCache.getBitmapFromMemoryCache(mUrl);
        /*view.get().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (mListView.get(mUrl.hashCode()) == null) {
                    return;
                }
                for (int i = 0; i < mListView.get(mUrl.hashCode()).size(); i++) {
                    if (mListView.get(mUrl.hashCode()).get(i) == callback) {
                        mListView.get(mUrl.hashCode()).remove(i);
                        view.get().removeOnAttachStateChangeListener(this);
                        return;
                    }
                }
            }
        });*/

        if (bitmap == null) {
            ArrayList<MyDownloadCallback> list;

            if (mListView.containsKey(mUrl.hashCode())) {
                list = mListView.get(mUrl.hashCode());
            } else {
                list = new ArrayList<>();
                seqNumber = seq.getAndIncrement() >= Integer.MAX_VALUE ? 0 : seq.getAndIncrement();
                DownloadImageRunnable downloadImageRunnable = new DownloadImageRunnable(mUrl, mHandler, seqNumber, imageCache, widthView, heightView, networkCheck);
                if (!networkCheck.isOnline()) {
                    onDownloadComplete(null, callback);
                    return;
                } else {
                    executor.execute(downloadImageRunnable);

                }
            }
            list.add(callback);
            mListView.put(mUrl.hashCode(), list);
        } else {
            onDownloadComplete(bitmap, callback);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        final int msTemp = msg.what;
        final Bitmap bitmap = (Bitmap) msg.obj;

        if (mListView.containsKey(msTemp)) {
            ArrayList<MyDownloadCallback> list = mListView.get(msTemp);
            for (int i = 0; i < list.size(); i++) {
                onDownloadComplete(bitmap, list.get(i));
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
