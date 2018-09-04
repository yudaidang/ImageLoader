package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.view.View;

import com.example.cpu11268.imageloader.ImageLoader.ImageWorker;

import java.lang.ref.WeakReference;

public class InfoImageView {
    private WeakReference<ImageWorker.MyDownloadCallback> callback;
    private WeakReference<View> view;

    public InfoImageView(ImageWorker.MyDownloadCallback callback, View view) {
        this.callback = new WeakReference<>(callback);
        this.view = new WeakReference<>(view);
    }

    public ImageWorker.MyDownloadCallback getCallback() {
        return callback.get();
    }

    public void setCallback(ImageWorker.MyDownloadCallback callback) {
        this.callback = new WeakReference<>(callback);
    }

    public WeakReference<View> getView() {
        return view;
    }

    public void setView(WeakReference<View> view) {
        this.view = view;
    }
}
