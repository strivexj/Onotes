package com.example.onotes.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cwj on 2017/3/9 19:56
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }

}
