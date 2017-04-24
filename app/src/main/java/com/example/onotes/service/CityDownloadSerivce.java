package com.example.onotes.service;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import com.example.onotes.App;
import com.example.onotes.datebase.CityDbHelper;
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

        CityDbHelper cityDbHelper = new CityDbHelper(App.getContext());
        SQLiteDatabase db = cityDbHelper.getWritableDatabase();
        String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS City";
        db.execSQL(SQL_DELETE_ENTRIES);
        cityDbHelper.onCreate(db);

        LogUtil.d("CWJ","servicequery");
        final String address = "https://cdn.heweather.com/china-city-list.json";
        WeatherUtil.queryFromServer(address);

    }
}
