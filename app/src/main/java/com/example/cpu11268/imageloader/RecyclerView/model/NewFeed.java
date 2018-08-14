package com.example.cpu11268.imageloader.RecyclerView.model;

public class NewFeed {
    private String mName;
    private String mPublish;
    private String mMessage;
    private String mUrlImage;

    public NewFeed(String mName, String mPublish, String mMessage, String mUrlImage) {
        this.mName = mName;
        this.mPublish = mPublish;
        this.mMessage = mMessage;
        this.mUrlImage = mUrlImage;
    }

    public String getmUrlImage() {
        return mUrlImage;
    }

    public void setmUrlImage(String mUrlImage) {
        this.mUrlImage = mUrlImage;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPublish() {
        return mPublish;
    }

    public void setmPublish(String mPublish) {
        this.mPublish = mPublish;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}
