package com.example.cpu11268.imageloader.RecyclerView.Holder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cpu11268.imageloader.ImageLoader.ImageWorker;
import com.example.cpu11268.imageloader.R;
import com.example.cpu11268.imageloader.RecyclerView.view_item.NewFeedItem;

public class NewFeedHolder extends BaseViewHolder<NewFeedItem> {
    private static int temp = 0;
    private static int temp1 = 0;

    public TextView mName;
    public TextView mTime;
    public TextView mMessage;
    public ImageView mAvatar;
    public TextView mId;
    String mLastUrl;
    private Context mContext;
    private ImageWorker imageWorker;


    public NewFeedHolder(View itemView, Context context, ImageWorker imageWorker) {
        super(itemView);
        this.imageWorker = imageWorker;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int mMaxSizeMem = (int) (
                (int) (displayMetrics.widthPixels / context.getResources().getDisplayMetrics().density) *
                        (displayMetrics.heightPixels / context.getResources().getDisplayMetrics().density)
                        * 4);// ARGB: 4, RGB: 3, BMP: 16, BMPS: 32

        imageWorker.setSizeSmallMemCache(mMaxSizeMem);
        imageWorker.setSizeLargeMemCache(mMaxSizeMem);
        temp++;
        mAvatar = itemView.findViewById(R.id.imageAvatar);
        mContext = context.getApplicationContext();
        mId = itemView.findViewById(R.id.idItem);

    }

    @Override
    public void onBind(final NewFeedItem item, int id) {
        super.onBind(item, id);
        temp1++;
        id++;

        mId.setText(id + "");

        mAvatar.setBackground(null);
        itemView.setTag(item);
        mLastUrl = id + "";
        final String idTemp = id + "";
        if (item != null) {
            ImageWorker.MyDownloadCallback img = new ImageWorker.MyDownloadCallback() {
                @Override
                public void onLoad(Bitmap bitmap) {
                    BitmapDrawable bm = new BitmapDrawable(mContext.getResources(), bitmap);
                    mAvatar.setBackground(bm);
                }
            };
//            imageWorker.loadImage(item.getmNewFeed().getmUrlImage(), mAvatar);
            imageWorker.cancelSameViewLoading(mAvatar);
            imageWorker.loadImage(item.getmNewFeed().getmUrlImage(), mAvatar, img);

        }
    }


}
