package com.example.cpu11268.imageloader.ImageLoader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.cpu11268.imageloader.ImageLoader.Ultils.ImageWorkerMain;
import com.example.cpu11268.imageloader.ImageLoader.Ultils.ValueBitmap;

import java.util.HashMap;
import java.util.Map;

public class ImageWorker extends ImageWorkerMain {//generic
    //Integer1: mUrl
    //Integer2: Sample Size
    //List nhung nhung sample size da duoc download voi cung 1 url
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
            Map.Entry<Integer, ImageKey> entry = list.entrySet().iterator().next();

            ImageKey im = entry.getValue();
            int sampleSize = caculateInSampleSize(im.getmOutWidth(), im.getmOutHeight(), imageKey.getSize(), imageKey.getSize());
            if (list.containsKey(sampleSize)) {
                bitmap = ImageCache.getInstance().findBitmapCache(imageKey.getSize(), imageKey.getSize(), imageKey.getmUrl());
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
            ImageCache.getInstance().addBitmapToMemoryCacheTotal(new ValueBitmap(bitmap, sampleSize, imageKey.getmUrl(), width, height)); //?
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
