package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashSet;

public class ImageWorker {//generic
    public static final int DEFAULT_MAX_SIZE = 0;

    protected ImageKey imageKey;

    public HashSet<MyDownloadCallback> listCallback = new HashSet<>();

    public ImageWorker(ImageKey imageKey) {
        this.imageKey = imageKey;
    }

    protected void onDownloadComplete(Bitmap bitmap) {
        if(listCallback!= null){
            for(MyDownloadCallback callback: listCallback){
                callback.onLoad(bitmap, null, 0);
            }
        }
        listCallback.clear();
    }

    public interface MyDownloadCallback {
        void onLoad(Bitmap bitmap, Object which, int resultCode);
    }

    public void setImageBitmap(byte[] bytes, BitmapFactory.Options options){
        boolean mMaxSize = false;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = caculateInSampleSize(options, imageKey.getSize(), imageKey.getSize());
        int width = options.outWidth;
        int height = options.outHeight;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        if (width == bitmap.getWidth() && height == bitmap.getHeight()) {
            mMaxSize = true;
        }
        onDownloadComplete(bitmap);
        ImageCache.getInstance().addBitmapToMemoryCacheTotal(imageKey, new ValueBitmapMemCache(bitmap, mMaxSize)); //?
    }

    private int caculateInSampleSize(BitmapFactory.Options options, int widthReq, int heightReq) {
        int inSampleSize = 1;
        while (((options.outHeight / 2) / inSampleSize) >= heightReq && ((options.outWidth / 2) / inSampleSize) >= widthReq) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }
}
