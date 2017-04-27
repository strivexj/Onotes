package com.example.onotes.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.onotes.R;

/**
 * Created by cwj on 4/27/17.
 */

public class TextHolder extends RecyclerView.ViewHolder {
    public EditText content;

    public TextHolder(View itemView) {
        super(itemView);

        content=(EditText)itemView.findViewById(R.id.edit_content);

    }
}
