package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkCheck;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageWorker implements Handler.Callback {
    private LruCache<String, Bitmap> mMemoryCache;
    private Executor executor = null; //?
    private ImageCache imageCache = null;
    private WeakReference<Context> context;
    private Handler mHandler;
    private int seqNumber; //? DownloadImageRunnable.seqNum?
    private AtomicInteger seq = new AtomicInteger(0);
    private WeakReference<ImageView> view;
    private NetworkCheck networkCheck; //? keep instance


    public ImageWorker(ImageView view /* //? generic */, Context context) {
        this.view = new WeakReference<>(view);
        mHandler = new Handler(this); //? which looper?
        this.context = new WeakReference<>(context);
        networkCheck = new NetworkCheck(new WeakReference<>(context).get() /* //? wtf? */);

        if (imageCache == null) {
            imageCache = ImageCache.getInstance(context);
        }

        if (executor == null) {
            WeakReference<PriorityBlockingQueue> priorityBlockingQueue /* //? WeakReference? */ = new WeakReference<PriorityBlockingQueue>(new PriorityBlockingQueue<Runnable>(1
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
            }));
            executor = new ThreadPoolExecutor(  //?
                    2,
                    3,
                    60L,
                    TimeUnit.SECONDS,

                    priorityBlockingQueue.get() /* //? WeakReference? */
            );
        }

    }


    public void loadImage(String mUrl) {
        seqNumber = seq.getAndIncrement(); //? overflow
        if (mUrl == null) {
            view.get().setImageBitmap(null); //? generic
            return;
        }

        int widthView = (int) (view.get().getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));
        int heightView = (int) (view.get().getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));


        Bitmap bitmap = imageCache.getBitmapFromMemoryCache(mUrl);
        if (bitmap == null) {
            DownloadImageRunnable downloadImageRunnable = new DownloadImageRunnable(mUrl, mHandler, seqNumber, imageCache, widthView, heightView, networkCheck);
            if (!networkCheck.isOnline()) { //? ^ v
                return; //? fall back?
            }
            executor.execute(downloadImageRunnable); //is running ?
        } else {
            view.get().setImageBitmap(bitmap); //? generic
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        final int threadId = msg.what;
        final Bitmap bitmap = (Bitmap) msg.obj;
        if (threadId == seqNumber && bitmap != null) {
            view.get().setImageBitmap(bitmap);
        }
        return false;
    }
}
