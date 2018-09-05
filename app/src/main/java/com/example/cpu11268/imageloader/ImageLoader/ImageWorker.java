package com.example.cpu11268.imageloader.ImageLoader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.CallBackImageView;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.InfoImageView;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.NetworkCheck;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.ValueBitmapMemCache;

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
    private NetworkCheck networkCheck;
    private String mUrlTemp;
    private int mWidth = DEFAULT_SIZE_SAMPLE;
    private int mHeight = DEFAULT_SIZE_SAMPLE;

    public ImageWorker(Context context) {
        mHandler = new Handler(this);
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

    public void setSizeSmallMemCache(int mMaxSize) {
        imageCache.setSizeSmallMem(mMaxSize);
    }

    public void setSizeLargeMemCache(int mMaxSize) {
        imageCache.setSizeLargeMem(mMaxSize);
    }

    private void onDownloadComplete(Bitmap bitmap, MyDownloadCallback myDownloadCallback) {
        if (myDownloadCallback != null) {
            myDownloadCallback.onLoad(bitmap);
        }
    }

    //handle view bị recycle trong recyclerview
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

    //handle call back bị recycle trong recyclerview
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

    //handle cancel khi 1 view đang download image 1 thì download image 2
    public void cancelSameViewLoading(View mView) {
        for (Integer mUrlHashCode : mListView.keySet()) {
            ArrayList<InfoImageView> list = mListView.get(mUrlHashCode);

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
        loadImage(mUrl, mView, new CallBackImageView(mView));
    }

    public void setSampleSize(int mWidth, int mHeight) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    /*
        View: width, height
        MyDownloadCallback: default is ImageView(CallBackImageView)
        ValueBitmapMemCache:
     */

    public void loadImage(final String mUrl, View mView, MyDownloadCallback callback) {
        if (mView != null) {
            this.mWidth = (int) (mView.getLayoutParams().width / (Resources.getSystem().getDisplayMetrics().density));
            this.mHeight = (int) (mView.getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));
        }
        this.mUrlTemp = mUrl;

        ValueBitmapMemCache valueBitmapMemCache;

        if (mUrl == null) {
            onDownloadComplete(null, callback);
            return;
        }

        valueBitmapMemCache = imageCache.getBitmapFromMemoryCache(mUrl);
        // Nếu valueBitmapMemCache đã lưu trong memcache chưa phải là valueBitmapMemCache có size lớn nhất và width * height lớn hơn default size.
        // width * height : lấy valueBitmapMemCache gốc.
        if (valueBitmapMemCache == null || (!valueBitmapMemCache.ismMaxSize() && (mWidth * mHeight > ImageCache.DEFAULT_MAX_SIZE || mWidth * mHeight == -1))) {
            valueBitmapMemCache = imageCache.getBitmapFromMemoryLargeCache(mUrl);
        }
        if (valueBitmapMemCache != null) {
            // Nếu valueBitmapMemCache đã lưu trong memcache chưa phải là valueBitmapMemCache có size lớn nhất
            if (!valueBitmapMemCache.ismMaxSize() && (valueBitmapMemCache.getmHeight() * valueBitmapMemCache.getmWidth() < mWidth * mHeight || mHeight * mWidth == -1)) {
                valueBitmapMemCache = null;
            }
        }

        if (valueBitmapMemCache == null) {
            ArrayList<InfoImageView> list;
            //Kiểm tra url có đang được download ko?
            if (mListView.containsKey(mUrl.hashCode())) {
                list = mListView.get(mUrl.hashCode());
            } else {
                list = new ArrayList<>();

                DiskBitmapRunnable diskBitmapRunnable = new DiskBitmapRunnable(mUrl, mHandler, seq.getAndIncrement() >= Integer.MAX_VALUE ? 0 : seq.getAndIncrement(),
                        imageCache, mWidth, mHeight, networkCheck);
                executor.execute(diskBitmapRunnable);

            }
            list.add(new InfoImageView(callback, mView));
            //add callback, view vào list đang được download với cùng url.
            mListView.put(mUrl.hashCode(), list);
        } else {
            onDownloadComplete(valueBitmapMemCache.getmBitmap(), callback);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        final int msTemp = msg.what;
        final Bitmap bitmap = (Bitmap) msg.obj;

        //kiểm tra list đang được download có chứa url này không?
        if (mListView.containsKey(msTemp)) {
            ArrayList<InfoImageView> list = mListView.get(msTemp);
            //return bitmap cho các callback với cùng url.
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
