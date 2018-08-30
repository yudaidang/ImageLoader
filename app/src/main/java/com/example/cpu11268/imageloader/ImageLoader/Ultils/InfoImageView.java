package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.view.View;

import com.example.cpu11268.imageloader.ImageLoader.ImageWorker;

public class InfoImageView {
    private ImageWorker.MyDownloadCallback callback;
    private View view;

    public InfoImageView(ImageWorker.MyDownloadCallback callback, View view) {
        this.callback = callback;
        this.view = view;
    }

    public ImageWorker.MyDownloadCallback getCallback() {
        return callback;
    }

    public void setCallback(ImageWorker.MyDownloadCallback callback) {
        this.callback = callback;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
