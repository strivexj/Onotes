package com.example.onotes.view;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.adapter.NotesAdapter;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.gson.Weather;
import com.example.onotes.setting.SettingActivity;
import com.example.onotes.utils.HttpUtil;
import com.example.onotes.utils.LogUtil;
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
    private List<String> list;
    private RecyclerView mRecyclerView;
    NotesAdapter adapter;
    private TextView weather_degree;
    private TextView weather_city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initData();
        //实例化并传输数据给adapterw
        adapter = new NotesAdapter(getApplicationContext(), list);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper.Callback mCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
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
                    //vib.vibrate(70);
                    //viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
                //当选中Item时候会调用该方法，重写此方法可以实现选中时候的一些动画逻辑
                Log.v("cwj", "onSelectedChanged");
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


       /* FloatingActionButton transfab=(FloatingActionButton)findViewById(R.id.transfab);
        transfab.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            }
        });

        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        //mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        // mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));*/

    }

    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

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
                    }
                }
                return true;
            }
        });

        SharedPreferences qqinfo = App.getContext().getSharedPreferences("qqaccount", MODE_PRIVATE);
        String qqusername = qqinfo.getString("nickname", "");
        String pictureurl = qqinfo.getString("figureurl_qq_2", "");
        LogUtil.d("ccwj", qqusername);
        LogUtil.d("ccwj", pictureurl);
        try {
            username.setText(qqusername);
            Glide.with(this).load(pictureurl).into(icon_image);
        } catch (Exception e) {
            e.printStackTrace();
        }


        setting = (TextView) findViewById(R.id.setting);

        setting.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

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

        weather_city.setOnClickListener(this);

         requestWeather();
       
    }

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
            case R.id.weather_city:
                CircularAnim.fullActivity(NotelistActivity.this, weather_city)
                        .colorOrImageRes(R.color.primary)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                startActivity(new Intent(NotelistActivity.this, WeatherMainActivity.class));
                            }
                        });

        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.category: {
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
                //Intent intent=new Intent(MainActivity.this,LoationActivity.class);
                // startActivityForResult(intent,0);
                break;
            }
            case R.id.delete: {

                // //Toast.makeText(MainActivity.this,"You clicked delete~!",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.settings: {
                //  Toast.makeText(MainActivity.this,"You clicked settings~!",Toast.LENGTH_SHORT).show();
                break;
            }
            case android.R.id.home: {
                // mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));


                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            }
            case R.id.list: {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                // Intent intent=new Intent(MainActivity.this,TranslateActivity.class);
                // startActivity(intent);
                break;
            }

            default:
        }
        return true;
    }

    private void initData() {
        list = new ArrayList<String>();
        for (int i = 1; i < 50; i++) {
            list.add(i + "只");
            LogUtil.d("cwj", "ji");
        }
    }

    private long clickTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
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
            Toast.makeText(getApplicationContext(), "再次点击退出", Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            this.finish();
            System.exit(0);
        }
    }

    public void requestWeather() {
        SharedPreferences pref = App.getContext().getSharedPreferences("weather", MODE_APPEND);
        String weatherid = pref.getString("weatherid", "");
        //https://free-api.heweather.com/v5/weather?city=yourcity&key=yourkey；
        //String weatherUrl = "https://free-api.heweather.com/v5/weather?city=" + weatherid + "&key=1e5bbb41868b4bce9f9586755e3a99e2";
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherid + "&key=1e5bbb41868b4bce9f9586755e3a99e2";

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = WeatherUtil.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            weather_city.setText(weather.basic.cityName);
                            weather_degree.setText(weather.now.temperature+"°C");
                        } else {
                            weather_city.setTextSize(10);
                            weather_city.setText("点击我选择城市吧");
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
                        weather_city.setText("点击我选择城市吧");
                        //Toast.makeText(NotelistActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}



