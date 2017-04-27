package com.example.onotes.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.onotes.R;

/**
 * Created by cwj on 4/27/17.
 */


public class MyRecyclerView extends RecyclerView {


    View view;
    private NotesAdapter.ViewHolder viewHolder;
    private LinearLayout itemLayout;
    private TextView deleteTextView;
    private int lastX;
    private int lastY;
    private int deleteWidth;
    private Scroller scroller;
    private int state = 0;//state=0表示侧滑菜单没有显示，state=1表示侧滑菜单显示出来了

    public MyRecyclerView(Context context) {
        super(context);
        scroller = new Scroller(context, new LinearInterpolator());
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context, new LinearInterpolator());
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        scroller = new Scroller(context, new LinearInterpolator());
    }

    //这里出现了事件分发的冲突，导致RecyclerView无法响应ACTION_DOWN事件，所以需要RecycleView进行该事件的劫持。
    //而且在滑动的过程当中，ACTION_MOVE事件也没有很好的响应，所以也把这个事件劫持了。
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchEvent(e);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchEvent(e);
                break;
            case MotionEvent.ACTION_UP:
                onTouchEvent(e);
                break;
        }
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int x = (int) e.getX();//获得当前点击的X坐标
        int y = (int) e.getY();//获得当前点击的Y坐标
        try {

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (state == 0) {//state=0表示菜单没被打开，state=1表示菜单被打开
                    view = findChildViewUnder(x, y);//根据用户点击的坐标，找到RecyclerView下的子View，这里也就是每一个Item

                        viewHolder = (NotesAdapter.ViewHolder) getChildViewHolder(view);//获得每一个Item的ViewHolder


                    itemLayout = viewHolder.layout;//获得ViewHolder相应的布局
                    deleteTextView = viewHolder.deleteTextView;//得到菜单栏里的控件，这里我们只有一个textview
                    deleteWidth = deleteTextView.getWidth();//获得侧滑菜单的宽度


                } else if (state == 1) {
                  //  try {
                        View view1 = findChildViewUnder(x, y);

                        NotesAdapter.ViewHolder viewHolder1 = (NotesAdapter.ViewHolder) getChildViewHolder(view1);

                        LinearLayout itemLayout1 = viewHolder1.layout;

                        Boolean bool = viewHolder.equals(viewHolder1);//判断当前用户指向的Item是否为之前打开的那个Item

                        if (bool) {
                            break;
                        } else {

                            scroller.startScroll(itemLayout.getScrollX(), 0, -deleteWidth, 0, 100);//弹性滑动
                            invalidate();
                            state = 0;


                            return true;   //加上这一句会好一些，
                        }

                  //  } catch (NullPointerException a) {
                     //   a.printStackTrace();
                  //  }

                }
                break;
            case MotionEvent.ACTION_MOVE:
                int scrollX = itemLayout.getScrollX();  //获得用户在滑动后，View相对初始位置移动的距离
                int dx = lastX - x; //得到用户实时移动的举例（横向）
                int dy = lastY - y;
                if (Math.abs(dx) > Math.abs(dy)) {  //只要左右移动的举例比上下移动的距离大，就执行滑动菜单操作
                    if (scrollX + dx >= deleteWidth) {    //检测右边界
                        itemLayout.scrollTo(deleteWidth, 0); //scrollTo()中的参数是指要“移动到的位置”
                        state = 1;
                        return true;    //表示已经消费这个事件，不必再传递了
                    } else if (scrollX + dx <= 0) {    //检测左边界
                        itemLayout.scrollTo(0, 0);
                        state = 0;
                        return true;
                    }
                    itemLayout.scrollBy(dx, 0);  //scrollBy()中的参数是指要“移动的距离（也就是像素的数量）”
                }
                break;
            case MotionEvent.ACTION_UP:
                int deltaX = 0;
                int upScrollX = itemLayout.getScrollX();//获得Item总共移动的距离
                Log.e("hehe", "scrollX2" + upScrollX);
                if (upScrollX >= deleteWidth / 2) {  //如果显示超过一半，则弹性滑开
                    deltaX = deleteWidth - upScrollX;
                    state = 1;
                } else if (upScrollX < deleteWidth / 2) {//否则关闭
                    deltaX = -upScrollX;//在startScroll()方法中，第三个参数小于0，表示向右滑。
                    state = 0;
                }
                scroller.startScroll(upScrollX, 0, deltaX, 0, 100);//弹性滑动
                invalidate();
                break;
        }

        } catch (NullPointerException a) {
            a.printStackTrace();
        }

        lastX = x;
        lastY = y;
        return super.onTouchEvent(e);//返回调用父类的方法，来处理我们没有处理的操作，比如上下滑动操作
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            itemLayout.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }
}
