package com.example.onotes.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import com.example.onotes.R;
import com.example.onotes.anim.CircularAnim;
import com.example.onotes.bean.MyUser;
import com.example.onotes.login.LoginActivity;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.LanguageUtil;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.SharedPreferenesUtil;
import java.util.Locale;

/**
 * 设置界面。。只做了切换APP语言和退出登录。。
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private Button logout;
    private RadioButton chinese;
    private RadioButton english;
    private RadioButton traditional_chinese;

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
        chinese = (RadioButton) findViewById(R.id.chinese);
        chinese.setOnClickListener(this);
        english = (RadioButton) findViewById(R.id.english);
        english.setOnClickListener(this);
        traditional_chinese = (RadioButton) findViewById(R.id.traditional_chinese);
        traditional_chinese.setOnClickListener(this);

        String language=SharedPreferenesUtil.getLanguage();
        if(language.equals("en")){
            english.setChecked(true);
        }else if(language.equals("zh_simple")){
            chinese.setChecked(true);
        }
        else if(language.equals("zh_tw")){
            traditional_chinese.setChecked(true);
        }else if(language.equals("default")){
            String default_language=Locale.getDefault().toString();
            if(default_language.equals("zh_CN")){
                chinese.setChecked(true);
            }else if(default_language.equals("en_US")){
                english.setChecked(true);
            } else if(default_language.equals("zh_TW")){
                traditional_chinese.setChecked(true);
        }
        }
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
                MyUser.logOut();
                SharedPreferenesUtil.setIssignin(false);
                LogUtil.d(this, "cwji");
                break;
            case R.id.chinese:
                LanguageUtil.switchLanguage(this, "zh_simple");
                break;
            case R.id.english:
                LanguageUtil.switchLanguage(this, "en");
                break;
            case R.id.traditional_chinese:
                LanguageUtil.switchLanguage(this, "zh_tw");
                break;

        }
    }



    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }
}
