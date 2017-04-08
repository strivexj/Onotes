package com.example.onotes.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.WeatherUtil;

/**
 * Created by cwj Apr.08.2017 8:09 PM
 */

public class CityDownloadSerivce extends IntentService{

    public CityDownloadSerivce() {
        super("CityDownloadService");
    }

    @Override
    public void onDestroy() {
        LogUtil.d("CWJ","DESTORY");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtil.d("CWJ","servicequery");
        final String address = "https://cdn.heweather.com/china-city-list.json";
        WeatherUtil.queryFromServer(address);

    }
}
