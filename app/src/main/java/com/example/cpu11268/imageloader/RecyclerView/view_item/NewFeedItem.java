package com.example.cpu11268.imageloader.RecyclerView.view_item;

import com.example.cpu11268.imageloader.RecyclerView.Ultils.ViewType;
import com.example.cpu11268.imageloader.RecyclerView.model.NewFeed;

public class NewFeedItem extends BaseViewItem {
    private NewFeed mNewFeed;

    public NewFeedItem(NewFeed mNewFeed) {
        this.mNewFeed = mNewFeed;
    }

    public NewFeed getmNewFeed() {
        return mNewFeed;
    }

    public void setmNewFeed(NewFeed mNewFeed) {
        this.mNewFeed = mNewFeed;
    }

    @Override
    public int getViewType() {
        return ViewType.NEW_FEEDS;
    }
}
