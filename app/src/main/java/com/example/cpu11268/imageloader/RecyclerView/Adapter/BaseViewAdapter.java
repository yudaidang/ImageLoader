package com.example.cpu11268.imageloader.RecyclerView.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cpu11268.imageloader.R;
import com.example.cpu11268.imageloader.RecyclerView.Holder.BaseViewHolder;
import com.example.cpu11268.imageloader.RecyclerView.Holder.NewFeedHolder;
import com.example.cpu11268.imageloader.RecyclerView.Ultils.ViewType;
import com.example.cpu11268.imageloader.RecyclerView.model.NewFeed;
import com.example.cpu11268.imageloader.RecyclerView.view_item.BaseViewItem;

import java.util.List;

public class BaseViewAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<BaseViewItem> mItems;

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
        holder = new NewFeedHolder(view, parent.getContext());

        return holder;
    }

    public void addItem(BaseViewItem item, int position) {
        this.mItems.add(position, item);
        notifyItemInserted(position);
    }

    public void changeItem(BaseViewItem item, int position) {
        this.mItems.remove(position);
        this.mItems.add(position, item);
        notifyItemChanged(position);
    }

    public void moveItem(int startposition, int endposition) {
        this.mItems.add(endposition, this.mItems.remove(startposition));

        notifyItemMoved(startposition, endposition);
    }

    public void removeItem(int position) {
        this.mItems.remove(position);
        notifyItemRemoved(position);
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
//                BaseViewAdapter.this.notifyItemChanged(position);
                NewFeed model = new NewFeed("Yudaidang", "2 hours", "ahihi", "http://iforo.3djuegos.com/files_foros/89/894.jpg");
//                BaseViewAdapter.this.removeItem(/*new NewFeedItem(model), */2);
//                BaseViewAdapter.this.notifyDataSetChanged();
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

    public List<BaseViewItem> getItems() {
        return mItems;
    }

    public void setItems(List<BaseViewItem> items) {
        mItems = items;
    }
}
