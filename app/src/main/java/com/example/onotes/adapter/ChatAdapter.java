package com.example.onotes.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.onotes.R;
import com.example.onotes.bean.Chat;

import java.util.List;

/**
 * Created by cwj Apr.13.2017 10:14 AM
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{

    private List<Chat>mList;
    private Context mContext;
    public static final int TYPE_MSG_FROM = 0;
    public static final int TYPE_MSG_TO = 1;

    public ChatAdapter(Context context, List<Chat> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        if (viewType == TYPE_MSG_FROM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_from, parent, false);
            holder = new ViewHolder(view);
        } else if (viewType == TYPE_MSG_TO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_to, parent, false);
            holder = new ViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ChatAdapter.ViewHolder holder, int position) {
        holder.mTextView.setText(mList.get(position).getContent());
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
        public ViewHolder(View itemView) {
            super(itemView);
            mTextView=(TextView)itemView.findViewById(R.id.content);
        }
    }


}
