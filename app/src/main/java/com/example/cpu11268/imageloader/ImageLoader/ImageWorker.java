package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkCheck;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageWorker<T extends ImageView> implements Handler.Callback {
    private static Executor executor;
    private static ImageCache imageCache = null;
    private static AtomicInteger seq = new AtomicInteger(0);
    private static ArrayList<Integer> mUrlDownloading = new ArrayList<>();
    //**
    private static HashMap<Integer, Deque<ImageView>> mListView1 = new HashMap<>();
    private static Handler mHandler;
    private final int DEFAULT_SIZE_LIST_IMAGEVIEW = 15;
    private int seqNumber;
    private WeakReference<Context> context;
    private WeakReference<T> view;
    private NetworkCheck networkCheck;
    //**

    public ImageWorker(T view, Context context) {
        this.view = new WeakReference<>(view);
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

    public void loadImage(String mUrl) {
        if (seq.getAndIncrement() >= Integer.MAX_VALUE) {
            seqNumber = 0;
        } else {
            seqNumber = seq.getAndIncrement();
        }
        if (mUrl == null) {
            view.get().setImageBitmap(null);
            return;
        }
        final int widthView = (int) (view.get().getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));
        final int heightView = (int) (view.get().getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));
        final Bitmap bitmap = imageCache.getBitmapFromMemoryCache(mUrl);
        if (bitmap == null) {
            if (mUrlDownloading.size() > 0 && mUrlDownloading.contains(mUrl.hashCode())) {
                if (!mListView1.containsKey(mUrl.hashCode())) {
                    Deque<ImageView> listStack = new ArrayDeque<>();
                    listStack.addLast(view.get());
                    mListView1.put(mUrl.hashCode(), listStack);
                } else {
                    Deque<ImageView> list = mListView1.get(mUrl.hashCode());
                    if (!list.contains(view.get())) {
                        while (list.size() > DEFAULT_SIZE_LIST_IMAGEVIEW) {
                            list.removeFirst();
                        }
                        list.addLast(view.get());
                    }
                }
            } else {
                mUrlDownloading.add(mUrl.hashCode());
                DownloadImageRunnable downloadImageRunnable = new DownloadImageRunnable(mUrlDownloading, mUrl, mHandler, seqNumber, imageCache, widthView, heightView, networkCheck);
                if (!networkCheck.isOnline()) {
                    view.get().setImageBitmap(null);
                } else {
                    executor.execute(downloadImageRunnable);
                }
            }
        } else {
            view.get().setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        final int threadId = msg.what;
        final Bitmap bitmap = (Bitmap) msg.obj;
        final int msTemp = msg.arg1;

        if (threadId == seqNumber && bitmap != null) {
            view.get().setImageBitmap(bitmap);
        }

        if (mListView1.containsKey(msTemp)) {
            Deque<ImageView> list = mListView1.get(msTemp);
            while (!list.isEmpty()) {
                list.pollLast().setImageBitmap(bitmap);
            }
        }

        if (mUrlDownloading.size() > 0 && mUrlDownloading.contains(msTemp)) {
            for (int i = 0; i < mUrlDownloading.size(); i++) {
                if (mUrlDownloading.get(i) == msTemp) {
                    mUrlDownloading.remove(i);
                }
            }
        }
        return false;
    }
}
