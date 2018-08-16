package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class BitmapPolicy {

    public boolean write(File outputFile, byte[] value) throws IOException {
        boolean success;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            BufferedOutputStream buf = new BufferedOutputStream(out);
            buf.write(value);
            success = true;
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
        return success;
    }

    public Bitmap read(File inputFile, int width, int height, BitmapFactory.Options options) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(inputFile.getAbsolutePath(), options);
        options.inSampleSize = caculateInSampleSize(options, width, height);
        options.inMutable = true;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(inputFile.getAbsolutePath(), options);
        return bitmap;
    }

    public Bitmap read(File inputFile) {
        WeakReference<Bitmap> bitmap = new WeakReference<>(BitmapFactory.decodeFile(inputFile.getAbsolutePath()));
        return bitmap.get();
    }

    private int caculateInSampleSize(BitmapFactory.Options options, int widthReq, int heightReq) {
        int inSampleSize = 1;
        while (((options.outHeight / 2) / inSampleSize) >= heightReq && ((options.outWidth / 2) / inSampleSize) >= widthReq) {
            inSampleSize *= 2;
        }
        return inSampleSize;
    }

    public long size(byte[] value) {
        return value.length;
    }
}
