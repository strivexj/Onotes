package com.example.onotes.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.utils.ActivityCollector;
import com.example.onotes.utils.SharedPreferenesUtil;


public class WeatherMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);
        ActivityCollector.addActivity(this);
        if (SharedPreferenesUtil.getWeatherresponseText() != null) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }

    }


}
