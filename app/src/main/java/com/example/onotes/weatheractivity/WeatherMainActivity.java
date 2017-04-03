package com.example.onotes.weatheractivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.example.onotes.R;
import com.example.onotes.datebase.CityDbHelper;


public class WeatherMainActivity extends AppCompatActivity {
    private CityDbHelper mCityDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);
        mCityDbHelper=new CityDbHelper(this);
        mCityDbHelper.getWritableDatabase();

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("weather",null)!=null){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();

        }
    }


}
