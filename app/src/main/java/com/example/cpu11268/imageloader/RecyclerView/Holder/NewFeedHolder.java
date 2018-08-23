package com.example.cpu11268.imageloader.RecyclerView.Holder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.cpu11268.imageloader.ImageLoader.ImageWorker;
import com.example.cpu11268.imageloader.R;
import com.example.cpu11268.imageloader.RecyclerView.view_item.NewFeedItem;

public class NewFeedHolder extends BaseViewHolder<NewFeedItem> implements Handler.Callback {
    private static final HandlerThread mHandlerThread;
    private static final Handler handler;
    private static int temp = 0;
    private static int temp1 = 0;

    static {
        mHandlerThread = new HandlerThread("HandlerThreadaaaa");
        mHandlerThread.start();
        handler = new Handler(mHandlerThread.getLooper());
    }

    public TextView mName;
    public TextView mTime;
    public TextView mMessage;
    public ImageView mAvatar;
    public TextView mId;
    String mLastUrl;
    Handler mainUiHandler;
    private Context mContext;
    private ImageWorker imageWorker = null;

    public NewFeedHolder(View itemView, Context context) {
        super(itemView);
        temp++;
        mAvatar = itemView.findViewById(R.id.imageAvatar);
        mContext = context.getApplicationContext();
        mId = itemView.findViewById(R.id.idItem);
        mainUiHandler = new Handler(this);
        if (imageWorker == null) {
            imageWorker = new ImageWorker(mAvatar, mContext);
        }
    }

    @Override
    public void onBind(final NewFeedItem item, int id) {
        super.onBind(item, id);
        temp1++;
        id++;

        mId.setText(id + "");

        mAvatar.setImageDrawable(null);
        itemView.setTag(item);
        mLastUrl = id + "";
        final String idTemp = id + "";
        Glide.with(mContext)
                .load(item.getmNewFeed().getmUrlImage())
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {

                    }
                });
        if (item != null) {
            imageWorker.loadImage(item.getmNewFeed().getmUrlImage(), idTemp);
        } else {
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        return true;
    }

}
