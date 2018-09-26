package com.example.cpu11268.imageloader.RecyclerView.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.cpu11268.imageloader.RecyclerView.view_item.BaseViewItem;

public class BaseViewHolder<T extends BaseViewItem> extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public void onBind(T item, int id) {

    }
    public void onRecycled() {

    }

    public void onDetachView(){

    }
}
