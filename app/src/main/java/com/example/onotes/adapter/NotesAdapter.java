package com.example.onotes.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.bean.Notes;
import com.example.onotes.datebase.NotesDbHelper;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.TimeUtil;
import com.example.onotes.view.EditTextActivity;
import java.io.File;
import java.util.List;



/**
 * Created by cwj Apr.04.2017 11:13 AM
 * 笔记列表适配器
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private Context mContext;
    private List<Notes> mList;
    private List<Integer> mHeight;


    private static final int Type_without_checkbox = -2;
    private static final int Type_with_checkbox = -3;

    private static final int Close_popupwindow = -4;

    private static final String onotesPictureStoreDirectory="Onotes";

    public NotesAdapter(Context context, List<Notes> list) {
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

       //判断是否需要显示checkbox
        if(viewType==Type_with_checkbox){
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.mCheckBox_delete.setVisibility(View.VISIBLE);
            return viewHolder;

        }else {
            ViewHolder viewHolder = new ViewHolder(view);
            viewHolder.mCheckBox_delete.setVisibility(View.GONE);
            return viewHolder;
        }
    }


    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //判断是否需要显示缩略图
        if(!mList.get(position).getThumbNail().equals("null")){

            String temp=mList.get(position).getContent();

            String []notation=mList.get(position).getPicture().split(",");

            //替代文本中的图片标记
            for (int j = 1; j <notation.length; j=j+2) {
                temp=temp.replace(notation[j],"");
                LogUtil.d("mList",notation[j]);
            }
            holder.contentTextView.setText(temp);

            LogUtil.d("mList",mList.get(position).getThumbNail());

            holder.note_thumbnail.setVisibility(View.VISIBLE);
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), onotesPictureStoreDirectory);
            Glide.with(App.getContext()).load(file+"/"+mList.get(position).getThumbNail().replace("null","")).into(holder.note_thumbnail);

        }else {
            holder.note_thumbnail.setVisibility(View.GONE);
            holder.contentTextView.setText(mList.get(position).getContent());
        }

        String timestamp= TimeUtil.getTime(false,TimeUtil.stringToDate(mList.get(position).getTime(),"yyyy-MM-dd HH:mm:ss").getTime());

        holder.time.setText(timestamp);
        // ViewGroup.LayoutParams lp = holder.mTextView.getLayoutParams();
        // lp.height = mHeight.get(position);


        holder.mCheckBox_delete.setChecked(mList.get(position).isCheckbox_delete());

        holder.content_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果没有checkbox单击为进入笔记详情页，否则选中该item
                if(mList.get(position).getType()==Type_without_checkbox){
                    LogUtil.d("click","without_");
                    gotoEditActivity(v, position);
                }
                else {
                    if( mList.get(position).isCheckbox_delete()){
                        mList.get(position).setCheckbox_delete(false);
                    }else {
                        mList.get(position).setCheckbox_delete(true);
                    }

                    sendBroadcastToNotesActivity();
                    notifyItemChanged(position);
                }

            }
        });

        holder.content_date.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //长按item时选中该item 并改变类型
                mList.get(position).setCheckbox_delete(true);
                switch_type(mList.get(position).getType());


                sendBroadcastToNotesActivity();

                return true;
            }
        });


        holder.mCheckBox_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mCheckBox_delete.isChecked()){
                    mList.get(position).setCheckbox_delete(true);
                }else {
                    mList.get(position).setCheckbox_delete(false);
                }

                sendBroadcastToNotesActivity();
            }
        });

        holder.deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //侧滑删除菜单删除note
                NotesDbHelper notesDbHelper = new NotesDbHelper(App.getContext());
                SQLiteDatabase db = notesDbHelper.getWritableDatabase();
                db.delete("Notes", "id=?", new String[]{mList.get(position).getId()+""});
                mList.remove(position);
                notifyDataSetChanged();
                db.close();

                if(mList.size()==0){
                    //通知关闭popupWindow
                    Intent intent = new Intent(EditTextActivity.REFRESH);
                    intent.putExtra("from_adapter",Close_popupwindow);
                    App.getContext().sendBroadcast(intent);
                }
            }
        });
    }

    /**
     * 更改最下方的已选中*项
     */
    private void sendBroadcastToNotesActivity() {
        Intent intent = new Intent(EditTextActivity.REFRESH);
        intent.putExtra("from_adapter",getSelectedSize());
        App.getContext().sendBroadcast(intent);
    }

    /**
     * 更改item类型，是否包含checkbox
     * @param type_now
     */
    public void switch_type(int type_now){
        int switchto;
        if(type_now==Type_with_checkbox){
           switchto=Type_without_checkbox;
        }else {
           switchto=Type_with_checkbox;
        }
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setType(switchto);
        }
        notifyDataSetChanged();

        Intent intent = new Intent(EditTextActivity.REFRESH);
        intent.putExtra("from_adapter",switchto);
        App.getContext().sendBroadcast(intent);

    }

    /**
     * 得到总共选中的item数量
     * @return
     */
    public int getSelectedSize(){
        int j=0;
        for(int i=0;i<mList.size();i++){
            if(mList.get(i).isCheckbox_delete()){
                j++;
            }
        }
        return j;
    }

    /**
     * 将所需note的信息存进intent并启动EditTextActivity
     * @param view
     * @param position
     */
    private void gotoEditActivity(View view, int position) {
        Intent intent = new Intent(view.getContext(), EditTextActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("content", mList.get(position).getContent());
        intent.putExtra("id", mList.get(position).getId());
        intent.putExtra("textsize", mList.get(position).getTextsize());
        intent.putExtra("linespace", mList.get(position).getLinespace());
        intent.putExtra("bgcolor",mList.get(position).getBgcolor());
        intent.putExtra("time",mList.get(position).getTime());
        intent.putExtra("insertpicture",mList.get(position).getPicture());
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

    /**
     * 原先写的删除全部选中的item的，好像后来写在Activity里了
     */
    public void checkbox_delete(){
        for(int i=0;i<mList.size();i++){
            if(mList.get(i).isCheckbox_delete()){
                NotesDbHelper notesDbHelper = new NotesDbHelper(App.getContext());
                SQLiteDatabase db = notesDbHelper.getWritableDatabase();
                db.delete("Notes", "id=?", new String[]{mList.get(i).getId()+""});
                mList.remove(i);
                notifyDataSetChanged();
                db.close();
            }
        }
    }

    //Item的ViewHolder以及Item内部布局控件的id绑定
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView contentTextView;

        public TextView time;

        public LinearLayout layout;

        public LinearLayout content_date;

        public TextView deleteTextView;

        public CheckBox mCheckBox_delete;

        public ImageView note_thumbnail;
        public ViewHolder(View itemView) {
            super(itemView);

            layout = (LinearLayout)itemView.findViewById(R.id.item_layout);

            content_date = (LinearLayout)itemView.findViewById(R.id.content_date);

            contentTextView = (TextView) itemView.findViewById(R.id.recycle_textview);

            deleteTextView = (TextView)itemView.findViewById(R.id.content_delete);

            time = (TextView) itemView.findViewById(R.id.notetime);

            mCheckBox_delete=(CheckBox)itemView.findViewById(R.id.checkbox_note);
            note_thumbnail=(ImageView)itemView.findViewById(R.id.note_thumbnail);

        }

    }

}
