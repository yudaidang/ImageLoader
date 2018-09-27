package com.example.cpu11268.imageloader.RecyclerView.Holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cpu11268.imageloader.ImageLoader.ImageLoader;
import com.example.cpu11268.imageloader.ImageLoader.ImageWorker;
import com.example.cpu11268.imageloader.R;
import com.example.cpu11268.imageloader.RecyclerView.view_item.NewFeedItem;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    public NewFeedHolder(View itemView, Context context) {
        super(itemView);
        temp++;
        mAvatar = itemView.findViewById(R.id.imageAvatar);
        mContext = context.getApplicationContext(); //?
        mId = itemView.findViewById(R.id.idItem);
        mName = itemView.findViewById(R.id.nameTest);
    }

    @Override
    public void onBind(final NewFeedItem item, int id) {
        super.onBind(item, id);
        temp1++;
        id++;

        mId.setText(id + "");
        mAvatar.setImageBitmap(null);
        itemView.setTag(item);
        mLastUrl = id + "";
        final String idTemp = id + "";
        if (item != null) {
/*            ImageWorker.MyDownloadCallback img = new ImageWorker.MyDownloadCallback() {
                @Override
                public void onLoad(Bitmap bitmap, Object which, int resultCode) {

                    BitmapDrawable bm = new BitmapDrawable(mContext.getResources(), bitmap);
                    mAvatar.setBackground(bm);
                }
            };
            if(listView.containsKey(mAvatar.hashCode())){
                ImageLoader.getInstance().clearCallback(listView.get(mAvatar.hashCode()));
            }
            listView.put(mAvatar.hashCode(), img);*/

//            ImageWorker imageWorker = new ImageWorker(item.getmNewFeed().getmUrlImage(), mAvatar);
/*            ImageWorker imageWorker = new ImageWorker(item.getmNewFeed().getmUrlImage(), img);
            imageWorker.setWidthHeight(40, 40);*/

            final String cachePath =
                    Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ?
                            Objects.requireNonNull(mContext.getExternalCacheDir()).getPath() :
                            mContext.getCacheDir().getPath();

            ImageLoader.getInstance().loadImageWorker(mContext, item.getmNewFeed().getmUrlImage(), mAvatar, cachePath + File.separator + "YUDAIDANG");
            mName.setText(item.getmNewFeed().getmName());

        }
    }


    @Override
    public void onRecycled() {
        super.onRecycled();
//        ImageLoader.getInstance().clearView(mAvatar);
    }

    @Override
    public void onDetachView() {
        super.onDetachView();
        ImageLoader.getInstance().clearTaskQueue(mAvatar);
        Log.d("onDetachView ", "DETACH ");

    }
}
