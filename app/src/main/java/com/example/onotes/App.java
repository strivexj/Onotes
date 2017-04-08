package com.example.onotes;

import android.app.Application;

import android.content.Context;
import android.util.Log;


public class App extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        Log.d("aa","sf");
       context=getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
}
