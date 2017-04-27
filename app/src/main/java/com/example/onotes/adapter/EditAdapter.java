package com.example.onotes.adapter;

/**
 * Created by cwj on 4/27/17.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.onotes.R;
import com.example.onotes.bean.Notes;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.view.EditTextActivity;

import java.util.List;



import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.onotes.R;
import com.example.onotes.bean.Notes;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.view.EditTextActivity;
import java.util.List;



/**
 * Created by cwj Apr.04.2017 11:13 AM
 */

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.ViewHolder> {
    private Context mContext;
    private List<Notes> mList;
    private List<Integer> mHeight;

    public EditAdapter(Context context, List<Notes> list) {
        mContext = context;
        mList = list;

     /*   mHeight = new ArrayList<Integer>();
        for (int i = 0; i < mList.size(); i++) {
            mHeight.add((int) (80 + Math.random() * 300));
        }*/
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //此处动态加载ViewHolder的布局文件并返回holder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //此处设置Item中view的数据
        holder.mTextView.setText(mList.get(position).getContent());
        holder.time.setText(mList.get(position).getTime());
        // ViewGroup.LayoutParams lp = holder.mTextView.getLayoutParams();
        // lp.height = mHeight.get(position);

        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoeditactivity(view, position);
            }
        });
        holder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoeditactivity(view, position);
            }
        });

    }

    private void gotoeditactivity(View view, int position) {
        Intent intent = new Intent(view.getContext(), EditTextActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("content", mList.get(position).getContent());
        intent.putExtra("id", mList.get(position).getId());
        intent.putExtra("textsize", mList.get(position).getTextsize());
        intent.putExtra("linespace", mList.get(position).getLinespace());
        intent.putExtra("bgcolor",mList.get(position).getBgcolor());
        intent.putExtra("time",mList.get(position).getTime());
        LogUtil.d("dbtextsize", mList.get(position).getTextsize() + "");
        LogUtil.d("dbtextspace", mList.get(position).getLinespace() + "");
        mList.remove(position);
        view.getContext().startActivity(intent);
    }

    @Override
    public int getItemCount() {
        //生成的Item的数量
        return mList.size();
    }

    //Item的ViewHolder以及Item内部布局控件的id绑定
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.recycle_textview);
            time = (TextView) itemView.findViewById(R.id.notetime);

        }

    }

}