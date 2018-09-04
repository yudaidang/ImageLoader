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

    private ImageWorker imageWorker = null;

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
        imageWorker.clearView(((NewFeedHolder) holder).mAvatar);
        Log.d("YUDAIDANGOVR ", "onViewRecycled " + ((NewFeedHolder) holder).mAvatar);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (imageWorker == null) {
            imageWorker = new ImageWorker(parent.getContext().getApplicationContext());
        }
        BaseViewHolder holder;

        if (viewType == TYPE_SMALL) {
            View view = inflater.inflate(R.layout.newfeeditem, parent, false);
            holder = new NewFeedHolder(view, parent.getContext(), imageWorker);

            return holder;
        } else if (viewType == TYPE_LARGE) {
            View view = inflater.inflate(R.layout.newfeedlargeitem, parent, false);
            holder = new NewFeedHolder(view, parent.getContext(), imageWorker);

            return holder;
        }
        return null;
    }


    @Override
    public void onBindViewHolder(@NonNull final BaseViewHolder holder, int position) {
        holder.onBind(mItems.get(position), position);
        Log.d("Yu BaseHolder: ", "onBindViewHolder " + position);
/*
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Log.d("Yu: ", position + " ");
                NewFeed model = new NewFeed("Yudaidang", "2 hours", "ahihi", "http://iforo.3djuegos.com/files_foros/89/894.jpg");
            }
        });*/
    }

    @Override
    public long getItemId(int position) {
//        return position == 0 ? 1 : super.getItemId(position);
        return position;
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return TYPE_SMALL;
        } else {
            return TYPE_LARGE;
        }
/*        if (mItems.get(position) != null)
            return mItems.get(position).getViewType();
        else
            return 10;*/
    }

}
