package com.example.onotes.gson;

/**
 * Created by cwj on 2017/3/9 19:55
 */
public class AQI {
    public AQICity city;

    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
