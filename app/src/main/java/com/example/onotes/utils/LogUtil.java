package com.example.onotes.utils;

import android.app.Activity;
import android.util.Log;

/**
 * Created by cwj Apr.07.2017 8:07 PM
 */

public class LogUtil {
    public static final int VERBOSE=1;
    public static final int DEBUG=2;
    public static final int INFO=3;
    public static final int WARN=4;
    public static final int ERROR=5;
    public static final int NOTHING=6;
    public static int level=VERBOSE;


    public static void v(Activity mActivity,String msg){
        if(level<=VERBOSE)
            Log.v(mActivity.getLocalClassName(),msg);
    }
    public static void d(Activity mActivity,String msg){
        if(level<=DEBUG)
            Log.d(mActivity.getLocalClassName(),msg);
    }
    public static void d(String ATG,String msg){
        if(level<=DEBUG)
            Log.d(ATG,msg);
    }
    public static void i(Activity mActivity,String msg){
        if(level<=INFO)
            Log.i(mActivity.getLocalClassName(),msg);
    }
    public static void w(Activity mActivity,String msg){
        if(level<=WARN)
            Log.w(mActivity.getLocalClassName(),msg);
    }
    public static void e(Activity mActivity,String msg){
        if(level<=ERROR)
            Log.e(mActivity.getLocalClassName(),msg);
    }
}
