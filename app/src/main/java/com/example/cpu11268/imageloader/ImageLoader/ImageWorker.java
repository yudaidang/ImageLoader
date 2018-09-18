package com.example.cpu11268.imageloader.ImageLoader;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.CallBackImageView;

public class ImageWorker {//generic
    public static final int DEFAULT_SIZE_SAMPLE = 0;

    protected String mUrl;
    protected MyDownloadCallback mCallback;
    protected int mWidth;
    protected int mHeight;
    protected View mView;

    public ImageWorker(String mUrl, ImageView mView) {
        this.mView = mView;
        this.mCallback = new CallBackImageView(mView);
        this.mUrl = mUrl;
        this.mWidth = (int) (mView.getLayoutParams().width / (Resources.getSystem().getDisplayMetrics().density));
        this.mHeight = (int) (mView.getLayoutParams().height / (Resources.getSystem().getDisplayMetrics().density));
    }

    public ImageWorker(String mUrl, MyDownloadCallback callback) {
        this.mUrl = mUrl;
        this.mCallback = callback;
    }

    public void setWidthHeight(int mWidth, int mHeight) {
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    protected void onDownloadComplete(Bitmap bitmap) {
        if (mCallback != null) {
            mCallback.onLoad(bitmap, null, 0);
        }
    }

    protected void onDownloadComplete(Bitmap bitmap, MyDownloadCallback callback) {
        if (callback != null) {
            callback.onLoad(bitmap, null, 0);
        }
    }

    public interface MyDownloadCallback {
        void onLoad(Bitmap bitmap, Object which, int resultCode);
    }
}
