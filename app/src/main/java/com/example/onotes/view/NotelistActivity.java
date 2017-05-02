package com.example.onotes.view;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.onotes.R;
import com.example.onotes.about.AboutActivity;
import com.example.onotes.adapter.MyRecyclerView;
import com.example.onotes.adapter.NotesAdapter;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.bean.Notes;
import com.example.onotes.datebase.NotesDbHelper;
import com.example.onotes.gson.Weather;
import com.example.onotes.service.ChatService;

import com.example.onotes.setting.SettingActivity;
import com.example.onotes.ui.PopUpActivity;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.HttpUtil;
import com.example.onotes.utils.LanguageUtil;
import com.example.onotes.utils.LocationUtil;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.SharedPreferenesUtil;
import com.example.onotes.utils.ToastUtil;
import com.example.onotes.utils.WeatherUtil;
import com.example.onotes.weather.WeatherMainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by cwj Apr.09.2017 1:38 PM
 */


public class NotelistActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private TextView username;
    private CircleImageView icon_image;
    private NavigationView navigationView;
    private TextView setting;
    private FloatingActionButton fab;
    private List<Notes> list = new ArrayList<>();
    private MyRecyclerView mRecyclerView;
    private NotesAdapter adapter;
    private TextView weather_degree;
    private TextView weather_city;
    private TextView saying;
    boolean serviceBound = false;
    private PopupWindow popupWindow;
    private float fabY;
    private boolean select_all = false;

    private TextView tv = null;

    private Toolbar toolbar = null;

    private Button select=null;
    private static final int Type_without_checkbox = -2;
    private static final int Type_with_checkbox = -3;

    private static final int Close_popupwindow = -4;


    //recyclerview_category
    private static final int Staggered_Grid_Layout = 1;

    private static final int  Linear_Layout = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        // String url=DebugDB.getAddressLog();
        // LogUtil.d("debugdb",url);

        mRecyclerView = (MyRecyclerView) findViewById(R.id.activity_main_recycle_view);

        if(SharedPreferenesUtil.getRecyclerview_category()==Staggered_Grid_Layout){
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }else{
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }


        ActivityCollector.addActivity(this);
        //实例化并传输数据给adapter
        adapter = new NotesAdapter(getApplicationContext(), list);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //  ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
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

                String notesid = list.get(position).getId() + "";
                NotesDbHelper notesDbHelper = new NotesDbHelper(NotelistActivity.this);
                SQLiteDatabase db = notesDbHelper.getWritableDatabase();

                db.delete("Notes", "id=?", new String[]{notesid});

                list.remove(position);
                adapter.notifyItemRemoved(position);


            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //滑动时改变Item的透明度
                    final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
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
                    //  vib.vibrate(70);
                    //viewHolder.itemView.setBackgroundColor(Color.LTGRAY);

                }
                //当选中Item时候会调用该方法，重写此方法可以实现选中时候的一些动画逻辑
                Log.v("cwj", "onSelectedChanged");
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                //  viewHolder.itemView.setBackgroundColor();
                //当动画已经结束的时候调用该方法，重写此方法可以实现恢复Item的初始状态
                Log.v("cwj", "clearView");
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        initData();
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        //mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        // mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));*/


    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d("cwj", "aonstart");
    }

    @Override
    protected void onResume() {

        initData();
        requestWeather();
        LogUtil.d("cwj", "aonresumeinit");
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closePopupwindow();

        LogUtil.d("cwj", "aonpause");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(refresh);
        LogUtil.d("cwj", "aondestory");
    }

    private void initView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();


        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }


        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);

        //如果在xml中用app:headerLayout="@layout/nav_header“会出现 不能引用nav_header中的widgets
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        final View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);

        username = (TextView) headerLayout.findViewById(R.id.username);
        icon_image = (CircleImageView) headerLayout.findViewById(R.id.icon_image);
        saying = (TextView) headerLayout.findViewById(R.id.saying);

        saying.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.weather: {
                        CircularAnim.fullActivity(NotelistActivity.this, navigationView)
                                .colorOrImageRes(R.color.primary)
                                .go(new CircularAnim.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        startActivity(new Intent(NotelistActivity.this, WeatherMainActivity.class));
                                    }
                                });
                        break;
                    }
                    case R.id.chat:
                        CircularAnim.fullActivity(NotelistActivity.this, navigationView)
                                .colorOrImageRes(R.color.primary)
                                .go(new CircularAnim.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        startActivity(new Intent(NotelistActivity.this, ChatActivity.class));
                                    }
                                });
                        break;
                    case R.id.file:
                        CircularAnim.fullActivity(NotelistActivity.this, navigationView)
                                .colorOrImageRes(R.color.primary)
                                .go(new CircularAnim.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        startActivity(new Intent(NotelistActivity.this, PhotoActivity.class));
                                    }
                                });
                        break;
                    case R.id.popupexercise:
                        CircularAnim.fullActivity(NotelistActivity.this, navigationView)
                                .colorOrImageRes(R.color.primary)
                                .go(new CircularAnim.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        startActivity(new Intent(NotelistActivity.this, PopUpActivity.class));
                                    }
                                });
                        break;

                    case R.id.about:
                        CircularAnim.fullActivity(NotelistActivity.this, navigationView)
                                .colorOrImageRes(R.color.primary)
                                .go(new CircularAnim.OnAnimationEndListener() {
                                    @Override
                                    public void onAnimationEnd() {
                                        startActivity(new Intent(NotelistActivity.this, AboutActivity.class));
                                    }
                                });
                        break;

                }
                return true;
            }
        });

        // SharedPreferences qqinfo = App.getContext().getSharedPreferences("qqaccount", MODE_PRIVATE);
        // String qqusername = qqinfo.getString("nickname", "");
        //String pictureurl = qqinfo.getString("figureurl_qq_2", "");
        // LogUtil.d("ccwj", qqusername);
        //  LogUtil.d("ccwj", pictureurl);
        username.setText(SharedPreferenesUtil.getNickname());

        if (!TextUtils.isEmpty(SharedPreferenesUtil.getFigureurl_qq_2())) {
            Glide.with(this).load(SharedPreferenesUtil.getFigureurl_qq_2()).into(icon_image);
        }


        setting = (TextView) findViewById(R.id.setting);

        setting.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                closePopupwindow();
                CircularAnim.fullActivity(NotelistActivity.this, fab)
                        .colorOrImageRes(R.color.primary)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                startActivity(new Intent(NotelistActivity.this, EditTextActivity.class));
                            }
                        });
            }
        });

        weather_degree = (TextView) findViewById(R.id.weather_degree);
        weather_degree.setOnClickListener(this);
        weather_city = (TextView) findViewById(R.id.weather_city);

        weather_city.setTextSize(15);
        weather_city.setText(R.string.top_to_authorise_locate);

        weather_city.setOnClickListener(this);

        requestWeather();
        register_refresh();

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.about,  /* "open drawer" description for accessibility */
                R.string.about  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                //  getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()


                LogUtil.d("draw", "close");
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle("shenm");
                closePopupwindow();
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                LogUtil.d("draw", "open");
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


    }

    private void closePopupwindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.setFocusable(true);
            popupWindow.dismiss();
            popupWindow=null;
        }
    }

    private void initData() {
        list.clear();
        LogUtil.d("onsaveread ", "aoninitdata");
        NotesDbHelper notesDbHelper = new NotesDbHelper(this);

        SQLiteDatabase db = notesDbHelper.getWritableDatabase();
        Cursor cursor = db.query("Notes", null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int i = 1;
            do {
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                float textsize = cursor.getFloat(cursor.getColumnIndex("textsize"));
                float linespace = cursor.getFloat(cursor.getColumnIndex("linespace"));
                int bgcolor = cursor.getInt(cursor.getColumnIndex("bgcolor"));
                //LogUtil.d("delete in",id+"");
                LogUtil.d("delete in" + id, content);
                Notes notes = new Notes();
                notes.setId(id);
                notes.setTime(time);
                notes.setContent(content);
                notes.setTextsize(textsize);
                notes.setLinespace(linespace);
                notes.setBgcolor(bgcolor);

                notes.setType(Type_without_checkbox);
                notes.setCheckbox_delete(false);

                list.add(notes);

                LogUtil.d("aonsaveread " + i, content);
                i++;
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        db.close();
        adapter.notifyDataSetChanged();


        Intent intentService = new Intent(this, ChatService.class);

        //bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intentService);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            ChatService.LocalBinder binder = (ChatService.LocalBinder) service;
            // player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting:
                CircularAnim.fullActivity(NotelistActivity.this, setting)
                        .colorOrImageRes(R.color.primary)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                startActivity(new Intent(NotelistActivity.this, SettingActivity.class));
                            }
                        });

                break;
            case R.id.weather_degree:
            case R.id.weather_city:

                if (ContextCompat.checkSelfPermission(NotelistActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && TextUtils.isEmpty(SharedPreferenesUtil.getWeatherid())) {
                    ActivityCompat.requestPermissions(NotelistActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                } else if (TextUtils.isEmpty(SharedPreferenesUtil.getWeatherid())) {
                    LocationUtil.startLocation();
                    weather_city.setText(R.string.locating);
                } else {
                    CircularAnim.fullActivity(NotelistActivity.this, weather_city)
                            .colorOrImageRes(R.color.primary)
                            .go(new CircularAnim.OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd() {
                                    startActivity(new Intent(NotelistActivity.this, WeatherMainActivity.class));
                                }
                            });
                }

                break;
            case R.id.saying:
                Toast.makeText(this, "TODO", Toast.LENGTH_LONG).show();
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 动态设置ToolBar状态
        int layout=SharedPreferenesUtil.getRecyclerview_category();

        if(layout==Staggered_Grid_Layout){
            menu.findItem(R.id.list).setVisible(true);
            menu.findItem(R.id.category).setVisible(false);
        }else {
            menu.findItem(R.id.list).setVisible(false);
            menu.findItem(R.id.category).setVisible(true);
        }


        return super.onPrepareOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {



        switch (item.getItemId()) {
            case R.id.category: {

                switchToolBarMenu(SharedPreferenesUtil.getRecyclerview_category());

                // a=true;

                break;
            }
            case R.id.delete: {

                if (popupWindow != null && popupWindow.isShowing()) {
                    if(select_all||list.size()==adapter.getSelectedSize()){
                        showAlertDialog();
                    }else {
                        delete_selected();
                    }
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setType(Type_with_checkbox);
                    }
                    adapter.notifyDataSetChanged();

                    showPopupwindow();


              /*  Snackbar snackbar=Snackbar.make(fab,"",Snackbar.LENGTH_LONG);
                View snackbarview=snackbar.getView();

                Snackbar.SnackbarLayout snackbarLayout=(Snackbar.SnackbarLayout)snackbarview;

                snackbarview.setBackgroundColor(getResources().getColor(R.color.transparent_black));
                View add_view= LayoutInflater.from(snackbarview.getContext()).inflate(R.layout.check_sheet,null);
                LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

                p.gravity=Gravity.CENTER_VERTICAL;

                snackbarLayout.addView(add_view,0,p);

                snackbar.show();
*/


                   // showAlertDialog();
                }
                break;
            }
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            }
            case R.id.list: {
                switchToolBarMenu(SharedPreferenesUtil.getRecyclerview_category());
               /* mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                invalidateOptionsMenu();

                SharedPreferenesUtil.setRecyclerview_category(Linear_Layout);*/



                break;
            }

            default:
        }
        return true;
    }

    private void switchToolBarMenu(int layout) {

        if(layout==Staggered_Grid_Layout){
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            invalidateOptionsMenu();
            SharedPreferenesUtil.setRecyclerview_category(Linear_Layout);
        }else {
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            invalidateOptionsMenu();
            SharedPreferenesUtil.setRecyclerview_category(Staggered_Grid_Layout);
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(NotelistActivity.this);
        dialog.setTitle(R.string.dialog_title);
        dialog.setMessage(R.string.dialog_content);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.warning);
        dialog.setPositiveButton(R.string.dialog_positive_button,new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog,int which)
            {
                NotesDbHelper notesDbHelper = new NotesDbHelper(NotelistActivity.this);
                SQLiteDatabase db = notesDbHelper.getWritableDatabase();
                String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS Notes";
                db.execSQL(SQL_DELETE_ENTRIES);
                notesDbHelper.onCreate(db);
                list.clear();
                adapter.notifyDataSetChanged();

                closePopupwindow();
            }
        });

        dialog.setNegativeButton(R.string.dialog_negative_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closePopupwindow();
            }
        });
        dialog.show();
    }

    private void showPopupwindow() {

        final View check_view = LayoutInflater.from(NotelistActivity.this).inflate(R.layout.check_sheet, null);

        popupWindow = new PopupWindow(check_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);


        popupWindow.showAtLocation(check_view, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

               /* final BottomSheetDialog dialog = new BottomSheetDialog(this);
                dialog.setContentView(check_view);
                dialog.show();

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        fab.animate().y(fabY).setDuration(400).start();
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setType(Type_without_checkbox);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });*/
        // popupWindow.getHeight();
        // fab.getHeight();
        // ToastUtil.showToast(fab.getY()+"a"+popupWindow.getHeight(),Toast.LENGTH_LONG);
        LogUtil.d("fab", fab.getY() + "a");

        tv = (TextView) check_view.findViewById(R.id.select_hint);

        final String checked_hint = getResources().getString(R.string.checked_hint);
        if(checked_hint.contains("items")){
            tv.setText(checked_hint.substring(0,checked_hint.length()-1));
        }else{
            tv.setText(checked_hint);
        }


        fabY = fab.getY();
        fab.animate().y(fab.getY() - 140).setDuration(400).start();


         select = (Button) check_view.findViewById(R.id.select_all);

        select.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (select_all) {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setCheckbox_delete(false);

                        if (!list.get(i).isCheckbox_delete()) {
                            LogUtil.d("adelete_unselect", i + "id " + list.get(i).getId());
                        }
                        // popupWindow.showAtLocation(check_view, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }
                    adapter.notifyDataSetChanged();
                    select.setText(R.string.select_all);
                    select_all = false;

                    if(checked_hint.contains("items")){
                        tv.setText(checked_hint.substring(0,checked_hint.length()-1));
                    }else{
                        tv.setText(checked_hint);
                    }
                   // tv.setText(checked_hint);
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setCheckbox_delete(true);

                        if (list.get(i).isCheckbox_delete()) {
                            LogUtil.d("adelete_select", i + "id " + list.get(i).getId());
                        }

                        // popupWindow.showAtLocation(check_view, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }

                    adapter.notifyDataSetChanged();
                    select.setText(R.string.unselected_all);
                    select_all = true;

                    if(list.size()==1||list.size()==0){
                        if(checked_hint.contains("items")) {
                            tv.setText(checked_hint.replace("0", list.size() + ""));
                            String delete_s = tv.getText().toString();
                            tv.setText(delete_s.substring(0, delete_s.length() - 1));
                        }else{
                            tv.setText(checked_hint.replace("0", list.size() + ""));
                        }
                    }
                    else {
                        tv.setText(checked_hint.replace("0", list.size() + ""));
                    }

                }

            }
        });

        check_view.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select_all||list.size()==adapter.getSelectedSize()){
                    showAlertDialog();
                }else {
                    delete_selected();
                }

            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                fab.animate().y(fabY).setDuration(400).start();
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setType(Type_without_checkbox);
                    // popupWindow.showAtLocation(check_view, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void delete_selected() {
        /*new Thread(new Runnable() {
            @Override
            public void run() {*/
        NotesDbHelper notesDbHelper = new NotesDbHelper(NotelistActivity.this);
        SQLiteDatabase db = notesDbHelper.getWritableDatabase();

        //   int size=;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isCheckbox_delete()) {
                LogUtil.d("adelete", i + "id" + list.get(i).getId());
                db.delete("Notes", "id=?", new String[]{list.get(i).getId() + ""});
            }

            // popupWindow.showAtLocation(check_view, Gravity.BOTTOM, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        db.close();

        //  runOnUiThread(new Runnable() {
        //     @Override
        //     public void run() {

        initData();
        adapter.notifyDataSetChanged();


        popupWindow.setFocusable(true);
        popupWindow.dismiss();
               /*     }
                });

            }
        }).start();*/
    }

    /* @Override
     public boolean dispatchTouchEvent(MotionEvent event){
         if(popupWindow!=null&&popupWindow.isShowing()){
             return false;
         }
         return super.dispatchTouchEvent(event);
     }*/
    private BroadcastReceiver refresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.d("textlocation", "onreceive");

            if (intent.getIntExtra("from_adapter", -1) != -1) {
                int a=intent.getIntExtra("from_adapter", -1);

                switch (a) {
                    case Type_with_checkbox:
                        LogUtil.d("switch", "with_checkbox");
                        showPopupwindow();
                        break;

                    case Close_popupwindow:
                    case Type_without_checkbox:
                        LogUtil.d("switch", "without_checkbox");
                        closePopupwindow();
                        break;


                    default:
                        if (tv != null) {
                            String hint = getResources().getString(R.string.checked_hint);
                            int size = intent.getIntExtra("from_adapter", -1);

                            LogUtil.d("onreceive",""+size);
                            if(list.size()==size&&select!=null){
                                select.setText(R.string.unselected_all);
                                select_all=true;
                            }else {
                                select.setText(R.string.select_all);
                                select_all=false;
                            }

                            //如果只有0或者1项，去掉英文中的s

                            if (size == 1 || size == 0) {
                                if(hint.contains("items")){
                                    tv.setText(hint.replace("0", size + ""));

                                    String delete_s = tv.getText().toString();
                                    tv.setText(delete_s.substring(0, delete_s.length() - 1));
                                }
                                else {
                                    tv.setText(hint.replace("0", size + ""));
                                }
                            } else {

                                tv.setText(hint.replace("0", size + ""));
                            }

                            break;
                        }
                }
            } else {

                LogUtil.d("onreceive","requestweather");
                initData();
                requestWeather();
            }

        }
    };

    private void register_refresh() {
        IntentFilter filter = new IntentFilter(EditTextActivity.REFRESH);
        registerReceiver(refresh, filter);
    }


    private long clickTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && popupWindow != null && popupWindow.isShowing()) {
            popupWindow.setFocusable(true);
            popupWindow.dismiss();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 3000) {
            Toast.makeText(getApplicationContext(), R.string.double_click_exit, Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            ActivityCollector.finishAll();
        }
    }

    public void requestWeather() {
        //  SharedPreferences pref = App.getContext().getSharedPreferences("weather", MODE_APPEND);
        // String weatherid = pref.getString("weatherid", "");

        //  LogUtil.d("onrequest1",weatherid);
        //https://free-api.heweather.com/v5/weather?city=yourcity&key=yourkey；
        //String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherid + "&key=1e5bbb41868b4bce9f9586755e3a99e2";
        //String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherid + "&key=1e5bbb41868b4bce9f9586755e3a99e2";
        String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + SharedPreferenesUtil.getWeatherid() + "&key=1e5bbb41868b4bce9f9586755e3a99e2";

        // ToastUtil.showToast(SharedPreferenesUtil.getWeatherid()+"   aaaaa",Toast.LENGTH_LONG);

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = WeatherUtil.handleWeatherResponse(responseText);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            //   weather_city.setText(weather.basic.cityName);
                            LogUtil.d("requestweather","succeed");
                            SharedPreferenesUtil.setWeatherresponseText(responseText);

                            if (SharedPreferenesUtil.getLanguage().equals("en")) {
                                weather_city.setText(SharedPreferenesUtil.getCityEn());
                            } else {
                                weather_city.setText(SharedPreferenesUtil.getCityZh());
                            }
                            weather_degree.setText(weather.now.temperature + "°C");

                        } else {

                            weather_city.setTextSize(15);
                          /*  if (!LocationUtil.startLocation()) {
                                weather_city.setText(R.string.top_to_authorise_locate);
                            }
*/
                            // weather_city.setText(R.string.click_me_select_city);
                            //weather_city.setText(R.string.click_me_select_city);
                            //Toast.makeText(NotelistActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weather_city.setTextSize(15);
                        //  weather_city.setText(R.string.click_me_select_city);
                        weather_city.setText(R.string.loading_weather_failed);
                        //Toast.makeText(NotelistActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意该权限才能定位喔", Toast.LENGTH_SHORT).show();

                            CircularAnim.fullActivity(NotelistActivity.this, weather_city)
                                    .colorOrImageRes(R.color.primary)
                                    .go(new CircularAnim.OnAnimationEndListener() {
                                        @Override
                                        public void onAnimationEnd() {
                                            startActivity(new Intent(NotelistActivity.this, WeatherMainActivity.class));
                                        }
                                    });
                            // finish();
                            return;
                        }
                        else if(result == PackageManager.PERMISSION_GRANTED){
                            LocationUtil.startLocation();
                            weather_city.setText(R.string.locating);
                        }
                    }
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}