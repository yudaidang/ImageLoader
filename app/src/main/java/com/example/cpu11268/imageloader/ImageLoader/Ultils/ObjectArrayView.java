package com.example.cpu11268.imageloader.ImageLoader.Ultils;

import android.view.View;

public class ObjectArrayView {
    private String mUrl;
    private View view;

    public ObjectArrayView(String mUrl, View view) {
        this.mUrl = mUrl;
        this.view = view;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
