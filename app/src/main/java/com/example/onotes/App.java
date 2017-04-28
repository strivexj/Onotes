package com.example.onotes;

import android.app.Application;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.onotes.utils.LanguageUtil;
import com.example.onotes.utils.MessageHandler;
import com.example.onotes.utils.SharedPreferenesUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;

import cn.bmob.newim.BmobIM;


public class App extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
       context=getApplicationContext();
        //只有主进程运行的时候才需要初始化
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            //im初始化
            BmobIM.init(this);
            //注册消息接收器
            BmobIM.registerDefaultMessageHandler(new MessageHandler());
        }

        Resources resources = App.getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = getSetLocale();
        resources.updateConfiguration(config, dm);



    }
    // 得到设置的语言信息
    private static Locale getSetLocale() {
       String language=SharedPreferenesUtil.getLanguage();
        if (language.equals("zh_simple")) {

            return Locale.SIMPLIFIED_CHINESE;

        }else if(language.equals("zh_tw")){

           return Locale.TRADITIONAL_CHINESE;

        } else if(language.equals("en")){

            return  Locale.ENGLISH;

        }else{

            return Locale.getDefault();

        }
    }
    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
}
    public static Context getContext(){
        return context;
    }
}
