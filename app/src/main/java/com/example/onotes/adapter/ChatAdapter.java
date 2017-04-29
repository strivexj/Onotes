package com.example.onotes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.bean.Chat;
import com.example.onotes.utils.SharedPreferenesUtil;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by cwj Apr.13.2017 10:14 AM
 */


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<Chat> mList;
    private Context mContext;
    public static final int TYPE_MSG_RIGHT = 0;
    public static final int TYPE_MSG_LEFT = 1;
    public static final int TYPE_PICTURE_LEFT = 2;
    public static final int TYPE_PICTURE_RIGHT = 3;

    public ChatAdapter(Context context, List<Chat> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        if (viewType == TYPE_MSG_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
            holder = new ViewHolder(view);

            String pictureurl= SharedPreferenesUtil.getFigureurl_qq_2();
            if (!TextUtils.isEmpty(pictureurl)) {
                Glide.with(App.getContext()).load(pictureurl).into(holder.mCircleImageView);
            }
            holder.mImageView.setVisibility(View.GONE);
            holder.mTextView.setVisibility(View.VISIBLE);

        } else if (viewType == TYPE_MSG_LEFT) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
            holder = new ViewHolder(view);
            holder.mImageView.setVisibility(View.GONE);
            holder.mTextView.setVisibility(View.VISIBLE);
        } else if (viewType == TYPE_PICTURE_LEFT) {
            holder.mImageView.setVisibility(View.GONE);
            holder.mTextView.setVisibility(View.VISIBLE);
        }
        else if(viewType==TYPE_PICTURE_LEFT){

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
            holder = new ViewHolder(view);
            Glide.with(App.getContext()).load(R.drawable.back).into(holder.mImageView);

            holder.mImageView.setVisibility(View.VISIBLE);
            holder.mTextView.setVisibility(View.GONE);
        } else if (viewType == TYPE_PICTURE_RIGHT) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
            holder = new ViewHolder(view);
            Glide.with(App.getContext()).load(R.drawable.back).into(holder.mImageView);

            holder.mImageView.setVisibility(View.VISIBLE);
            holder.mTextView.setVisibility(View.GONE);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {
        holder.mTextView.setText(mList.get(position).getContent());
        String pictureurl = mList.get(position).getPictureurl();

        if(!TextUtils.isEmpty(pictureurl)){

            Glide.with(App.getContext()).load(pictureurl).into(holder.mCircleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }




    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView mTextView;
        ImageView mImageView;
        CircleImageView mCircleImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.content);
            mImageView = (ImageView) itemView.findViewById(R.id.item_image);
            mCircleImageView = (CircleImageView) itemView.findViewById(R.id.picture);
        }
    }


}
