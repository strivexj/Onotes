package com.example.onotes.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.onotes.R;

/**
 * Created by cwj on 4/27/17.
 * 还没写。。
 */

public class ImageHolder  extends RecyclerView.ViewHolder{
    public ImageView edit_image;
    public ImageHolder(View itemView) {
        super(itemView);
        edit_image=(ImageView)itemView.findViewById(R.id.edit_image);
    }
}
