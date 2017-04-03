package com.example.onotes.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cwj on 2017/3/9 19:48
 */
public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

   public class  Update{

       @SerializedName("loc")
       public String updateTime;
    }
}
