package com.example.onotes.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.login.LoginActivity;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.view.EditTextActivity;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActivityCollector.addActivity(this);
        initView();
    }

    private void initView() {
        logout = (Button) findViewById(R.id.logout);

        logout.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout:
                CircularAnim.fullActivity(SettingActivity.this, logout)
                        .colorOrImageRes(R.color.primary)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {
                                startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                            }
                        });

                SharedPreferences.Editor editor= App.getContext().getSharedPreferences("account",MODE_PRIVATE).edit();
                editor.putBoolean("issignin",false);
                editor.apply();
                LogUtil.d(this,"cwji");
                break;
        }
    }


    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
