package com.example.onotes.view;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.datebase.CityDbHelper;
import com.example.onotes.datebase.NotesDbHelper;
import com.example.onotes.login.LoginActivity;
import com.example.onotes.setting.SettingActivity;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.KeyboardUtil;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.weather.WeatherActivity;
import com.example.onotes.weather.WeatherMainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditTextActivity extends AppCompatActivity {

    private SeekBar linespacing;
    private SeekBar textsize;
    private EditText edittext;
    public static final String REFRESH="com.example.onotes.refresh";
    private String notesid;
    private float linespace=0;
    private float textsizef=25;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        LogUtil.d("cwj","oncerate");
        ActivityCollector.addActivity(this);


        LogUtil.d("time",System.currentTimeMillis()+"");

       LogUtil.d("time",getcurrenttime());

        // this work
       // this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        // Check if no view has focus:

       /*View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }*/

        //InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
       // keyboard.hideSoftInputFromWindow(getWindow().getAttributes().token, 0);


        initView();
        //edittext.setTextSize(25);
        linespacing.setMax(1000);
        textsize.setMax(100);
    }

    private String getcurrenttime() {
        Calendar calendar= Calendar.getInstance();
        return calendar.get(Calendar.YEAR)+"."+
                calendar.get(Calendar.MONTH)+"."
                + calendar.get(Calendar.DAY_OF_MONTH)+" "+
                        calendar.get(Calendar.HOUR_OF_DAY)+"."+
                                calendar.get(Calendar.MINUTE)+ "."+calendar.get(Calendar.SECOND);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d("cwj","edonpause");
    }

    private void initView() {


        linespacing = (SeekBar) findViewById(R.id.linespacing);
        textsize = (SeekBar) findViewById(R.id.textsize);
        edittext = (EditText) findViewById(R.id.edittext);

        linespacing.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                edittext.setLineSpacing((float) progress, 1);

                linespace=(float)progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        textsize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                edittext.setTextSize(progress);
                textsizef=(float)progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        edittext.setText(load());
        edittext.post(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(edittext.getText().toString())) {
                    edittext.setSelection(edittext.getText().toString().length());
                    LogUtil.d("cwj", "set");
                }
            }
        });
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Spannable inputStr = (Spannable)s;
                if(s.equals("草")){
                    inputStr.setSpan(new ForegroundColorSpan(Color.BLUE),start,start+count, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Spannable inputStr = (Spannable)s;
                if(s.equals("草")){
                    inputStr.setSpan(new ForegroundColorSpan(Color.BLUE),0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            }
        });
        LogUtil.d("cwj","length:"+edittext.getText().toString().length());
        textsize.setProgress((int)textsizef);
       linespacing.setProgress((int)linespace);
    }
    public String load(){
        Intent intent=getIntent();
        notesid=intent.getIntExtra("id",-1)+"";
        textsizef=intent.getFloatExtra("textsize",25);
        linespace=intent.getFloatExtra("linespace",0);
        edittext.setTextSize(textsizef);
        edittext.setLineSpacing(linespace, 1);


        LogUtil.d("load",textsize+"  "+linespace);
        return intent.getStringExtra("content");
    }
    public void save(String data){
        if(!TextUtils.isEmpty(data)) {
            NotesDbHelper notesDbHelper = new NotesDbHelper(this);
            SQLiteDatabase db = notesDbHelper.getWritableDatabase();
            db.delete("Notes","id=?",new String[]{notesid});
            LogUtil.d("delete",notesid+"");
            ContentValues values = new ContentValues();
            values.put("textsize",textsizef);
            values.put("linespace",linespace);
            values.put("content", data);
            values.put("time",getcurrenttime());
            db.insert("Notes", null, values);
            db.close();
            LogUtil.d("textsize",edittext.getTextSize()+"");
            LogUtil.d("texts",""+linespace);
        }
        Intent intent=new Intent(REFRESH);
        sendBroadcast(intent);
    }



    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d("cwj","edonstart");

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d("cwj","edonstop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d("cwj","edonresume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("cwj","edondestory");
       save(edittext.getText().toString());
    }
}
