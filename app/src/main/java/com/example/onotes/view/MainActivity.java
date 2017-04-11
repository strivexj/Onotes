package com.example.onotes.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.onotes.R;
import com.example.onotes.adapter.NotesAdapter;
import com.example.onotes.login.LoginActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<String> list;
    private RecyclerView mRecyclerView;
    NotesAdapter adapter;
    private Activity mActivity=this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRecyclerView=(RecyclerView)findViewById(R.id.activity_main_recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initData();
        //实例化并传输数据给adapterw
        adapter=new NotesAdapter(getApplicationContext(),list);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.LEFT) {
            /**
             * @param recyclerView
             * @param viewHolder 拖动的ViewHolder
             * @param target 目标位置的ViewHolder
             * @return
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();//得到拖动ViewHolder的position
                int toPosition = target.getAdapterPosition();//得到目标ViewHolder的position
                if (fromPosition < toPosition) {
                    //分别把中间所有的item的位置重新交换
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(list, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(list, i, i - 1);
                    }
                }
                adapter.notifyItemMoved(fromPosition, toPosition);
                //返回true表示执行拖动
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                list.remove(position);
                adapter.notifyItemRemoved(position);
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) / (float)viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                }
            }
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    //获取系统震动服务
                    //Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                    //震动70毫秒
                    //vib.vibrate(70);
                    //viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
                //当选中Item时候会调用该方法，重写此方法可以实现选中时候的一些动画逻辑
                Log.v("cwj","onSelectedChanged");
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // viewHolder.itemView.setBackgroundColor(0);
                //当动画已经结束的时候调用该方法，重写此方法可以实现恢复Item的初始状态
                Log.v("cwj", "clearView");
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);



        /*FloatingActionButton transfab=(FloatingActionButton)findViewById(R.id.transfab);
        transfab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            }
        });
*/
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
       //mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        // mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
    }

    private void initData() {
        list=new ArrayList<String>();
        for(int i=1;i<50;i++){
            list.add(i+"只");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add:
                mRecyclerView.smoothScrollToPosition(list.size()-1);
                // adapter.addData(1);
                break;
            case R.id.menu_remove:
                // adapter.RemoveData(1);

                break;
        }
        return true;
    }

}
