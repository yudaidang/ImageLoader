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
import com.example.cpu11268.imageloader.ImageLoader.Ultils.MessageBitmap;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.ObjectArrayView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.Executor;

public class ImageWorker implements Handler.Callback {//generic
    public static final int DEFAULT_SIZE_SAMPLE = -1;
    private final Handler mHandler;
    private Executor executor;
    private Executor executorInternet;

    private HashMap<MyDownloadCallback, ObjectArrayView> mListCallbackKey;
    private HashMap<String, HashMap<MyDownloadCallback, View>> mListViewPlus;
    private HashMap<View, MyDownloadCallback> mListView;
    private String mUrl;
    private MyDownloadCallback mCallback;
    private int mWidth;
    private int mHeight;
    private View view;
    private String idTemp;

    public ImageWorker(Executor executor, Executor executorInternet, HashMap<MyDownloadCallback, ObjectArrayView> mListCallbackKey,
                       HashMap<String, HashMap<MyDownloadCallback, View>> mListViewPlus,
                       HashMap<View, MyDownloadCallback> mListView) {
        mHandler = new Handler(this);
        this.mListView = mListView;
        this.mListViewPlus = mListViewPlus;
        this.mListCallbackKey = mListCallbackKey;
        this.executor = executor;
        this.executorInternet = executorInternet;
    }



    private void onDownloadComplete(Bitmap bitmap, MyDownloadCallback myDownloadCallback, String idTemp) {
        if (myDownloadCallback != null) {
            Log.d("KKKKKK", idTemp + " ");
            myDownloadCallback.onLoad(bitmap, null, 0);
        }
    }


    public void setInfoImageWorker(String mUrl, MyDownloadCallback callback) {
        this.mUrl = mUrl;
        this.mCallback = callback;
    }

    public void setInfoImageWorker(String mUrl, ImageView imageView) {
        this.mUrl = mUrl;
        this.view = imageView;
        if (imageView != null) {
            this.mWidth = (int) (imageView.getLayoutParams().width / (Resources.getSystem().getDisplayMetrics().density));
            this.mHeight = (int) (imageView.getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));
        }
        this.mCallback = new CallBackImageView(imageView);
    }

    public void setInfoImageWorker(int mWidth, int mHeight) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    public void loadImage(Context context, String id) {
        this.idTemp = id;
        Bitmap bitmap;

        if (mUrl == null) {
            onDownloadComplete(null, mCallback, id);
            return;
        }

        bitmap = ImageCache.getInstance().findBitmapCache(mUrl, mWidth, mHeight);

        if (bitmap == null) {
            HashMap<MyDownloadCallback, View> mList = mListViewPlus.containsKey(mUrl) ? mListViewPlus.get(mUrl) : new HashMap<MyDownloadCallback, View>();
            mList.put(mCallback, view);
            if (!mListViewPlus.containsKey(mUrl)) {
                DiskBitmapRunnable diskBitmapRunnable = new DiskBitmapRunnable(executorInternet, context, mUrl, mHandler,
                        mWidth, mHeight);
                executor.execute(diskBitmapRunnable);
            }
            mListCallbackKey.put(mCallback, new ObjectArrayView(mUrl, view));
            if (view != null) {
                mListView.put(view, mCallback);
            }
            mListViewPlus.put(mUrl, mList);
            Log.d("jkkkkkkkkkkkkkkkk", id + " " +mUrl + " " + mCallback + " " + view);


        } else {
            onDownloadComplete(bitmap, mCallback, id);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == DownloadImageRunnable.IMAGE_DOWNLOAD_RESULT_CODE || msg.what == DiskBitmapRunnable.IMAGE_LOADED_FROM_DISK_RESULT_CODE) {

            MessageBitmap messageBitmap = (MessageBitmap) msg.obj;
            if (messageBitmap.ismMaxSize() && mWidth == 0 && mHeight == 0) {
                mWidth = messageBitmap.getmBitmap().getWidth();
                mHeight = messageBitmap.getmBitmap().getHeight();
            }
            ImageCache.getInstance().addBitmapToMemoryCacheTotal(new ImageKey(messageBitmap.getmUrl(), mWidth, mHeight), new ValueBitmapMemCache(messageBitmap.getmBitmap(), messageBitmap.ismMaxSize())); //?

            //kiểm tra list đang được download có chứa url này không?
            if (mListViewPlus.containsKey(messageBitmap.getmUrl())) {
                HashMap<MyDownloadCallback, View> list = mListViewPlus.get(messageBitmap.getmUrl());
                ArrayList listKey = new ArrayList(list.keySet());
                for(int i = listKey.size() - 1; i>=0; i--){
                    MyDownloadCallback callback = (MyDownloadCallback) listKey.get(i);
                    View view = list.get(callback);
                    onDownloadComplete(messageBitmap.getmBitmap(), callback, idTemp);
                    if(mListView.containsKey(view)){
                        mListView.remove(view);
                    }
                    mListCallbackKey.remove(callback);
                }
                list.clear();
                listKey.clear();
                mListViewPlus.remove(messageBitmap.getmUrl());

            }
            return true;
        }
        return false;
    }

    public interface MyDownloadCallback {
        void onLoad(Bitmap bitmap, Object which, int resultCode);
    }
}
