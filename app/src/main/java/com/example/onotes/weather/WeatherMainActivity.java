package com.example.onotes.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.example.onotes.App;
import com.example.onotes.R;


public class WeatherMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_main);

        SharedPreferences prefs = App.getContext().getSharedPreferences("weather",MODE_PRIVATE);
        if (prefs.getString("weatherresponseText", null) != null) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }

    }


}