package com.example.cpu11268.imageloader.RecyclerView.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cpu11268.imageloader.ImageLoader.ImageWorker;
import com.example.cpu11268.imageloader.R;
import com.example.cpu11268.imageloader.RecyclerView.Holder.BaseViewHolder;
import com.example.cpu11268.imageloader.RecyclerView.Holder.NewFeedHolder;
import com.example.cpu11268.imageloader.RecyclerView.view_item.BaseViewItem;

import java.util.List;

public class BaseViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<BaseViewItem> mItems;

    private int TYPE_SMALL = 0;

    private int TYPE_LARGE = 1;


    public BaseViewAdapter(List<BaseViewItem> mItems) {
        this.mItems = mItems;
        setHasStableIds(true);
    }
    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onRecycled();
        Log.d("YUDAIDANGOVR ", "onViewRecycled " + ((NewFeedHolder) holder).mAvatar);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onDetachView();

    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        BaseViewHolder holder;

//        if (viewType == TYPE_SMALL) {
            View view = inflater.inflate(R.layout.newfeeditem, parent, false);
            holder = new NewFeedHolder(view, parent.getContext());

            return holder;
    /*    } else if (viewType == TYPE_LARGE) {
            View view = inflater.inflate(R.layout.newfeedlargeitem, parent, false);
            holder = new NewFeedHolder(view, parent.getContext());

            return holder;
        }
        return null;*/
    }


    @Override
    public void onBindViewHolder(@NonNull final BaseViewHolder holder, int position) {
        holder.onBind(mItems.get(position), position);
        Log.d("Yu BaseHolder: ", "onBindViewHolder " + position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
/*        if (position % 2 == 0) {
            return TYPE_SMALL;
        } else {
            return TYPE_LARGE;
        }*/
        if (mItems.get(position) != null)
            return mItems.get(position).getViewType();
        else
            return 10;
    }


}
