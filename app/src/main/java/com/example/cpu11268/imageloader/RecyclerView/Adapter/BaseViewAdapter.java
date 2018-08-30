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
import com.example.cpu11268.imageloader.RecyclerView.Ultils.ViewType;
import com.example.cpu11268.imageloader.RecyclerView.model.NewFeed;
import com.example.cpu11268.imageloader.RecyclerView.view_item.BaseViewItem;

import java.util.List;

public class BaseViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ImageWorker imageWorker = null;

    private List<BaseViewItem> mItems;



    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {
        super.onViewRecycled(holder);
        imageWorker.clearView(((NewFeedHolder) holder).mAvatar);
        Log.d("YUDAIDANGOVR ", "onViewRecycled " + ((NewFeedHolder) holder).mAvatar);
    }

    public BaseViewAdapter(List<BaseViewItem> mItems) {
        this.mItems = mItems;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Yu BaseHolder: ", "onCreateViewHolder ");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.newfeeditem, parent, false);

        BaseViewHolder holder = null;

        switch (viewType) {
            case ViewType.NEW_FEEDS:

                view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        Log.d("Yu ", "Attach");
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {
                        Log.d("Yu ", "Detach");

                    }
                });
                break;
        }


        if (imageWorker == null) {
            imageWorker = new ImageWorker(parent.getContext().getApplicationContext());
        }

        holder = new NewFeedHolder(view, parent.getContext(), imageWorker);

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final BaseViewHolder holder, int position) {
        holder.onBind(mItems.get(position), position);
        Log.d("Yu BaseHolder: ", "onBindViewHolder " + position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                Log.d("Yu: ", position + " ");
                NewFeed model = new NewFeed("Yudaidang", "2 hours", "ahihi", "http://iforo.3djuegos.com/files_foros/89/894.jpg");
            }
        });
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
        if (mItems.get(position) != null)
            return mItems.get(position).getViewType();
        else
            return 10;
    }

}
