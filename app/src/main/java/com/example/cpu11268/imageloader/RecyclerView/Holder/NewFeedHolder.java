package com.example.cpu11268.imageloader.RecyclerView.Holder;

import android.content.Context;
import android.os.Handler;
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
    Handler mainUiHandler;
    private Context mContext;
    private ImageWorker imageWorker;


    public NewFeedHolder(View itemView, Context context, ImageWorker imageWorker) {
        super(itemView);
        this.imageWorker = imageWorker;
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
/*            ImageWorker.MyDownloadCallback img = new ImageWorker.MyDownloadCallback() {
                @Override
                public void onLoad(Bitmap bitmap) {
                    BitmapDrawable bm = new BitmapDrawable(mContext.getResources(), bitmap);
                    mAvatar.setBackground(bm);
                }
            };*/
            imageWorker.loadImage(item.getmNewFeed().getmUrlImage(), mAvatar);
/*            imageWorker.cancelSameViewLoading(mAvatar);
            imageWorker.loadImage(item.getmNewFeed().getmUrlImage(), mAvatar, img);*/

        }
    }


}
