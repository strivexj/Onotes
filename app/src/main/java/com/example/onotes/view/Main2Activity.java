package com.example.onotes.view;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.onotes.R;
import com.example.onotes.adapter.ChatAdapter;
import com.example.onotes.bean.Chat;
import com.example.onotes.datebase.ChatDbHelper;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.LogUtil;
import com.turing.androidsdk.HttpRequestListener;
import com.turing.androidsdk.TuringManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private TuringManager mTuringManager;
    private String turingKey = "d7670b6895a745f9a50bea198cfeb1f8";
    private String secret = "abc1e88f6f592c43";
    private EditText write;
    private Button request;
    private RecyclerView mRecyclerView;
    private ChatAdapter adapter;
    private List<Chat> data = new ArrayList<>();
    private int initial;
    public static final int TYPE_MSG_RIGHT = 0;
    public static final int TYPE_MSG_LEFT = 1;
    public static final int TYPE_PICTURE_LEFT = 2;
    public static final int TYPE_PICTURE_RIGHT = 3;
    private ImageView sendpicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ActivityCollector.addActivity(this);
        if (ContextCompat.checkSelfPermission(Main2Activity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
        initView();
        initdata();
    }

    private void initView() {


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.arrowleft);
        }
        mTuringManager = new TuringManager(this, turingKey,
                secret);

        mTuringManager.setHttpRequestListener(new HttpRequestListener() {

            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String text = jsonObject.get("text").toString();
                    adddata(TYPE_MSG_LEFT, text);
                    // tv.setText(text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int i, String s) {
                adddata(TYPE_MSG_LEFT, "sending failed");
            }
        });

        write = (EditText) findViewById(R.id.write);
        write.setOnClickListener(this);
        write.requestFocus();

        request = (Button) findViewById(R.id.request);
        request.setOnClickListener(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.chat_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        //实例化并传输数据给adapter
        adapter = new ChatAdapter(getApplicationContext(), data);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.scrollToPosition(data.size() - 1);
                            // mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
                        }
                    }, 100);
                }
            }
        });

        sendpicture = (ImageView) findViewById(R.id.sendpicture);
        sendpicture.setOnClickListener(this);
    }

    private void initdata() {
        LogUtil.d("cwj", "initdata");


        ChatDbHelper chatDbHelper = new ChatDbHelper(this);
        SQLiteDatabase db = chatDbHelper.getWritableDatabase();
        Cursor cursor = db.query("Chat", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String content = cursor.getString(cursor.getColumnIndex("content"));
                int type = cursor.getInt(cursor.getColumnIndex("type"));
                Chat chat = new Chat();
                chat.setType(type);
                chat.setContent(content);
                data.add(chat);
            } while (cursor.moveToNext());
            adapter.notifyDataSetChanged();
        }
        initial = data.size();
        cursor.close();
        if (data.size() > 0)
            mRecyclerView.smoothScrollToPosition(data.size() - 1);

    }

    private void adddata(int type, String content) {
        LogUtil.d("cwj", "adddata");
        if (type == TYPE_MSG_LEFT) {
            Chat chat = new Chat();
            chat.setContent(content);
            chat.setType(TYPE_MSG_LEFT);
            data.add(chat);
            adapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(data.size() - 1);
        }
        if (type == TYPE_MSG_RIGHT) {
            Chat chat = new Chat();
            chat.setContent(content);
            chat.setType(TYPE_MSG_RIGHT);
            data.add(chat);
            adapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(data.size() - 1);
        }

    }


    private void savedata() {
        LogUtil.d("cwj", "savedata");
        ChatDbHelper chatDbHelper = new ChatDbHelper(this);
        SQLiteDatabase db = chatDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        for (int i = initial; i < data.size(); i++) {
            String content = data.get(i).getContent();
            int type = data.get(i).getType();
            values.put("type", type);
            values.put("content", content);
            db.insert("Chat", null, values);
        }
        db.close();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
             onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request:
                adddata(TYPE_MSG_RIGHT, write.getText().toString());
                mTuringManager.requestTuring(write.getText().toString());
                write.setText("");
                break;
            case R.id.sendpicture:
                Chat chat = new Chat();
                chat.setContent("");
                chat.setType(TYPE_PICTURE_RIGHT);
                data.add(chat);
                adapter.notifyDataSetChanged();
                mRecyclerView.smoothScrollToPosition(adapter.getItemCount());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        savedata();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
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
