package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapPolicy {

    public void write(File outputFile, byte[] value) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputFile);
            BufferedOutputStream buf = new BufferedOutputStream(out);
            buf.write(value);
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    public Bitmap read(File inputFile, int width, int height, BitmapFactory.Options options) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(inputFile.getAbsolutePath(), options);
        options.inSampleSize = caculateInSampleSize(options, width, height);
        options.inMutable = true;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(inputFile.getAbsolutePath(), options);
    }

    public Bitmap read(File inputFile) {
        return BitmapFactory.decodeFile(inputFile.getAbsolutePath());
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
