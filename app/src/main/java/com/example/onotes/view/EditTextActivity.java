package com.example.onotes.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.login.LoginActivity;
import com.example.onotes.setting.SettingActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class EditTextActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    private SeekBar linespacing;
    private SeekBar textsize;
    private EditText edittext;

    private NavigationView navigationView;

    private SideBar mSideBar;

    private TextView setting;

    private TextView username;
    private CircleImageView icon_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_text);
        LogUtil.d("cwj","oncerate");
        initView();

        linespacing.setMax(1000);
        textsize.setMax(100);

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d("cwj","edonpause");
    }

    private void initView() {

       // mDrawerLayout = (DrawerLayout) findViewById(R.id.etdrawer_layout);
        linespacing = (SeekBar) findViewById(R.id.linespacing);
        textsize = (SeekBar) findViewById(R.id.textsize);
        edittext = (EditText) findViewById(R.id.edittext);

        linespacing.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                edittext.setLineSpacing((float) progress, 1);
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
                edittext.setSelection(edittext.getText().toString().length()-1);
                LogUtil.d("cwj","set");
            }
        });
        LogUtil.d("cwj","length:"+edittext.getText().toString().length());
        Toast.makeText(this, edittext.getText().toString()+"", Toast.LENGTH_SHORT).show();

    }

    public void save(String data){
        LogUtil.d("cwj","before"+data);
        FileOutputStream outputStream=null;
        BufferedWriter writer=null;
        try{
            outputStream=openFileOutput("data", Context.MODE_PRIVATE);
            writer=new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(data);
            LogUtil.d("cwj","after"+data);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
                try{
                    if(writer!=null){
                        writer.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        edittext.setText(load());

    }

    public EditTextActivity() {
        super();
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
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        save(edittext.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d("cwj","edondestory");
        save(edittext.getText().toString());

    }

    public String load(){
        FileInputStream inputStream=null;
        BufferedReader reader=null;
        StringBuilder content=new StringBuilder();
        try{
            inputStream=openFileInput("data");
            reader=new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            while ((line=reader.readLine())!=null){
                content.append(line);
                content.append("\n");
            }
            LogUtil.d("cwj","load");
        }catch (IOException e){
            e.printStackTrace();
        }finally {

                try{
                    if(reader!=null) {
                        reader.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

        return  content.toString();
    }


   /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK){
            LogUtil.d("cwj","back");
            save(edittext.getText().toString());
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }*/
   private long clickTime = 0;

   /* @Override
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
    }*/

}
