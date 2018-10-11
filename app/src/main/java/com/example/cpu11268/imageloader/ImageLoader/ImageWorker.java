package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.ValueBitmap;

import java.util.HashMap;
import java.util.HashSet;

public class ImageWorker {//generic
    public static final int DEFAULT_MAX_SIZE = 0;
    public HashSet<MyDownloadCallback> listCallback = new HashSet<>(); //arrayset
    public ImageKey imageKey;
    protected HashMap<String, Pair<Integer, Integer>> mListDecoded = new HashMap<>();

    public ImageWorker(ImageKey imageKey) {
        this.imageKey = imageKey;
    }

    public void onDownloadComplete(Bitmap bitmap, int resultCode) {
        if (listCallback != null) {
            for (ImageWorker.MyDownloadCallback callback : listCallback) {
                try {
                    callback.onLoad(bitmap, null, resultCode);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            listCallback.clear();
        }
    }

    private Bitmap decode(byte[] bytes, BitmapFactory.Options options) {
        Bitmap bitmap;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        int sampleSize = caculateInSampleSize(options.outWidth, options.outHeight, imageKey.getSize(), imageKey.getSize());
        options.inSampleSize = sampleSize;
        int width = options.outWidth;
        int height = options.outHeight;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        imageKey.setmOutHeight(height);
        imageKey.setmOutWidth(width);
        ImageCache.getInstance().addBitmapToMemoryCache(new ValueBitmap(bitmap, sampleSize, imageKey.getmUrl(), width, height)); //?
        mListDecoded.put(imageKey.getmUrl(), new Pair<>(width, height));
        return bitmap;
    }

    private Bitmap find() {
        Bitmap bitmap = null;
        Pair<Integer, Integer> mWH;
        if (mListDecoded.containsKey(imageKey.getmUrl())) {
            mWH = mListDecoded.get(imageKey.getmUrl());
            int sampleSize = caculateInSampleSize(mWH.first, mWH.second, imageKey.getSize(), imageKey.getSize());
            bitmap = ImageCache.getInstance().findBitmapCache(sampleSize, imageKey.getmUrl());
        }
        return bitmap;
    }

    public void decodeDataBitmap(byte[] bytes, BitmapFactory.Options options, int resultCode) {
        Bitmap bitmap;
        bitmap = find();
        if (bitmap == null) {
            mListDecoded.remove(imageKey.getmUrl());
            bitmap = decode(bytes, options);
        } else {
            Log.d("IMAGELOADERLOG", "LOAD BITMAP WITH MEMORY");

        }
        onDownloadComplete(bitmap, resultCode);
    }

    private int caculateInSampleSize(int outWidth, int outHeight, int widthReq, int heightReq) {
        int inSampleSize = 1;
        while (((outHeight / 2) / inSampleSize) >= heightReq && ((outWidth / 2) / inSampleSize) >= widthReq) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    public interface MyDownloadCallback {
        void onLoad(Bitmap bitmap, Object which, int resultCode);
    }

}
