package com.example.onotes.utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.onotes.bean.City;
import com.example.onotes.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 10032 on 2017/3/9.
 */

public class Utility {



    /**
     * parse JSON date to Weather class
     */
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            //Log.d("cwj","weatherContent"+weatherContent);
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Parse and handle the data of city from server
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCitys=new JSONArray(response);
                for(int i=0;i<allCitys.length();i++){
                    JSONObject cityObject=allCitys.getJSONObject(i);
                    City city=new City();

                }
                Log.d("cwj","add all city");
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
   /**
     * Parse and handle the data of Province from server
     */
   /*  public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvinces=new JSONArray(response);
                for(int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObjcet=allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObjcet.getString("name"));
                    province.setProvinceCode(provinceObjcet.getInt("id"));
                    province.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }*/


    /**
     * Parse and handle the data of county from server
     */
   /* public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCountys=new JSONArray(response);
                for(int i=0;i<allCountys.length();i++){
                    JSONObject countyObject=allCountys.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }*/

}
