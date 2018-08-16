package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkCheck;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageWorker<T extends ImageView> implements Handler.Callback {
    private static Executor executor; //?
    private static ImageCache imageCache = null;
    private WeakReference<Context> context;
    private final Handler mHandler;
    private int seqNumber; //? DownloadImageRunnable.seqNum?: OK
    private AtomicInteger seq = new AtomicInteger(0);
    private WeakReference<T> view;
    private NetworkCheck networkCheck; //? keep instance OK

    public ImageWorker(T view, Context context) {/* //? generic : OK*/
        this.view = new WeakReference<>(view);
        mHandler = new Handler(this); //? which looper?: OK
        this.context = new WeakReference<>(context);
        networkCheck = NetworkCheck.getInstance(context);/*new NetworkCheck(context *//* //? wtf?: OK *//*);*/

        if (imageCache == null) {
            imageCache = ImageCache.getInstance(context);
        }

        if (executor == null) {
            PriorityBlockingQueue priorityBlockingQueue /* //? WeakReference?:OK */ = new PriorityBlockingQueue<Runnable>(1
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
            executor = new ThreadPoolExecutor(  //?
                    2,
                    3,
                    60L,
                    TimeUnit.SECONDS,

                    priorityBlockingQueue /* //? WeakReference?:OK */
            );
        }

    }


    public void loadImage(String mUrl) {
        if(seq.getAndIncrement() >= Integer.MAX_VALUE){
            seqNumber = 0;
        }else {
            seqNumber = seq.getAndIncrement();
        }
        //? overflow
        if (mUrl == null) {
            view.get().setImageBitmap(null); //? generic: OK
            return;
        }

        int widthView = (int) (view.get().getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));
        int heightView = (int) (view.get().getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));

        Bitmap bitmap = imageCache.getBitmapFromMemoryCache(mUrl);
        if (bitmap == null) {
            DownloadImageRunnable downloadImageRunnable = new DownloadImageRunnable(mUrl, mHandler, seqNumber, imageCache, widthView, heightView, networkCheck);
            if (!networkCheck.isOnline()) { //? ^ v
                view.get().setImageBitmap(null); //?   back?: OK : generic
            } else {
                executor.execute(downloadImageRunnable); //is running ?
            }
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
