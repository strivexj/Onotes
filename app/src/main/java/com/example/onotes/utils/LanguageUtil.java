package com.example.onotes.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.example.onotes.App;
import com.example.onotes.setting.SettingActivity;
import com.example.onotes.view.NotelistActivity;

import java.util.Locale;

/**
 * Created by cwj on 4/28/17.
 * 切换语言工具类
 */

public class LanguageUtil {

    public static void switchLanguage(Activity activity, String language) {



        //设置应用语言类型

        Resources resources = App.getContext().getResources();

        Configuration config = resources.getConfiguration();

        DisplayMetrics dm = resources.getDisplayMetrics();

        if (language.equals("zh_simple")) {

            config.locale = Locale.SIMPLIFIED_CHINESE;

        }else if(language.equals("zh_tw")){

            config.locale = Locale.TRADITIONAL_CHINESE;

        } else if(language.equals("en")){

            config.locale = Locale.ENGLISH;

        }else{

            config.locale = Locale.getDefault();

        }

        resources.updateConfiguration(config, dm);

//保存设置语言的类型

        SharedPreferenesUtil.setLanguage(language);


//更新语言后，destroy当前页面，重新绘制
        activity.finish();


        Intent it = new Intent(activity, NotelistActivity.class);

        //清空任务栈确保当前打开activity为前台任务栈栈顶

        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       // it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(it);

    }
}
