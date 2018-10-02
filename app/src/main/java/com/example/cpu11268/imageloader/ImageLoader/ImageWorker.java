package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.ImageWorkerMain;

import java.util.HashMap;

public class ImageWorker extends ImageWorkerMain {//generic

    protected HashMap<Integer, HashMap<Integer, ImageKey>> mListDecoded = new HashMap<>();

    public ImageWorker(ImageKey imageKey) {
        super(imageKey);
    }

    public void setImageBitmap(byte[] bytes, BitmapFactory.Options options, int resultCode) {
        boolean mMaxSize = false;
        Bitmap bitmap = null;
        HashMap<Integer, ImageKey> list;
        if (mListDecoded.get(imageKey.getmUrl().hashCode()) != null) {
            list = mListDecoded.get(imageKey.getmUrl().hashCode());
            int sampleSize = caculateInSampleSize(imageKey.getmOutWidth(), imageKey.getmOutHeight(), imageKey.getSize(), imageKey.getSize());
            if (list.get(sampleSize) != null) {
                bitmap = ImageCache.getInstance().findBitmapCache(list.get(sampleSize));
            }
            if (bitmap == null) {
                list.remove(sampleSize);
            }
        } else {
            list = new HashMap<>();
        }
        if (bitmap == null) {
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
            list.put(sampleSize, imageKey);
            if (width == bitmap.getWidth() && height == bitmap.getHeight()) {
                mMaxSize = true;
            }
            ImageCache.getInstance().addBitmapToMemoryCacheTotal(imageKey, new ValueBitmapMemCache(bitmap, mMaxSize)); //?
        }
        mListDecoded.put(imageKey.getmUrl().hashCode(), list);
        onDownloadComplete(bitmap, resultCode);
    }

    private int caculateInSampleSize(int outWidth, int outHeight, int widthReq, int heightReq) {
        int inSampleSize = 1;
        while (((outHeight / 2) / inSampleSize) >= heightReq && ((outWidth / 2) / inSampleSize) >= widthReq) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

}
