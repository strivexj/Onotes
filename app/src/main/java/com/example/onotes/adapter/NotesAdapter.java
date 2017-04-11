package com.example.onotes.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onotes.R;
import com.example.onotes.gson.Weather;
import com.example.onotes.login.LoginActivity;
import com.example.onotes.view.EditTextActivity;
import com.example.onotes.weather.WeatherActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


/**
 * Created by cwj Apr.04.2017 11:13 AM
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>{
       private Context mContext;
        private List<String>mList;
        private List<Integer> mHeight;
        private CardView mCardView;


        public NotesAdapter( Context context, List<String> list) {
                mContext = context;
                mList = list;

            mHeight = new ArrayList<Integer>();
            for (int i = 0; i < mList.size(); i++) {
                mHeight.add((int) (80 + Math.random() * 300));
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                //此处动态加载ViewHolder的布局文件并返回holder
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_item,parent,false);
                ViewHolder viewHolder=new ViewHolder(view);

                return viewHolder;
        }





    @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
                //此处设置Item中view的数据
                holder.mTextView.setText(mList.get(position));

                ViewGroup.LayoutParams lp = holder.mTextView.getLayoutParams();
                lp.height = mHeight.get(position);

                holder.mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(view.getContext(), EditTextActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);

                    Toast.makeText(mContext, mList.get(position),Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public int getItemCount() {
                //生成的Item的数量
                return mList.size();
        }
        //Item的ViewHolder以及Item内部布局控件的id绑定
        class  ViewHolder extends RecyclerView.ViewHolder{
                TextView mTextView;
                public ViewHolder(View itemView) {
                        super(itemView);
                        mTextView=(TextView)itemView.findViewById(R.id.recycle_textview);

                }

        }

    public void addData(int position){

        mList.add(position, "新增" + position);
        //通知适配器item内容插入
        notifyItemInserted(position);
    }
    public void RemoveData(int position){
        mList.remove(position);
        //通知适配器item内容删除
        notifyItemRemoved(position);
    }
}
