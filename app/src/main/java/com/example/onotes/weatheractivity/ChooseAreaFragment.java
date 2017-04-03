package com.example.onotes.weatheractivity;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.onotes.R;
import com.example.onotes.bean.City;
import com.example.onotes.datebase.CityDbHelper;
import com.example.onotes.utils.HttpUtil;
import com.example.onotes.utils.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by cwj on 2017/3/9 13:52
 */
public class ChooseAreaFragment extends Fragment{
    public static final int LEVEL_CITY=1;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String>adapter;
    private List<String>dataList=new ArrayList<>();
    private City selectedCity;

    /**
     * city list
     */
    private List<City>cityList;
/**
 * id : CN101010100
 * cityEn : beijing
 * cityZh : 北京
 * provinceEn : beijing
 * provinceZh : 北京
 * leaderEn : beijing
 * leaderZh : 北京
 * lat : 39.904989
 * lon : 116.405285
 */
    /**
     * query all cities in the selected province,and prior to query from database,otherwise query from server
     */
    private void queryCities(){

        //titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        CityDbHelper cityDbHelper=new CityDbHelper(getActivity());
        SQLiteDatabase db=cityDbHelper.getWritableDatabase();
        Cursor cursor=db.query("City",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                City city=new City();
                city.setId(cursor.getString(cursor.getColumnIndex("cityid")));
                city.setCityEn(cursor.getString(cursor.getColumnIndex("CityEn")));
                city.setCityZh(cursor.getString(cursor.getColumnIndex("setCityZh")));
                city.setProvinceEn(cursor.getString(cursor.getColumnIndex("provinceEn")));
                city.setProvinceZh(cursor.getString(cursor.getColumnIndex("provinceZh")));
                city.setLeaderEn(cursor.getString(cursor.getColumnIndex("leaderEn")));
                city.setLeaderZh(cursor.getString(cursor.getColumnIndex("leaderZh")));
                city.setLat(cursor.getString(cursor.getColumnIndex("lat")));
                city.setLon(cursor.getString(cursor.getColumnIndex("lon")));
                cityList.add(city);
            }while(cursor.moveToNext());
        }
        cursor.close();
        if(cityList.size()>0){
            Log.d("cwj","b");
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityEn());
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                //currentLevel=LEVEL_CITY;
            }
        }else{
            Log.d("cwj","a");
            //int provinceCode=selectedProvince.getProvinceCode();
            //String address="http://guolin.tech/api/china/"+provinceCode;
            String address="https://cdn.heweather.com/china-city-list.json";
            queryFromServer(address,"city");

        }
    }
    /**
     * according to the address and type poured in,query data from server
     */
   // @TargetApi(23)
    private  void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //return main thread to handle logic through runOnUiThread()
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity().getApplicationContext(),"loading failed",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result=false;
                /*if("province".equals(type)){
                    result= Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){

                }else if("county".equals(type)){
                    result=Utility.handleCountyResponse(responseText,selectedCity.getId());
                }*/
                 result=Utility.handleCityResponse(responseText,getActivity());
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                           /* if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){*/
                                queryCities();
                           /* }else if("county".equals(type)){
                                queryCounties();
                            }*/
                        }
                    });
                }
            }

        });
    }
    /**
     * show progress dialog
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * close progress dialog
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    @TargetApi(23)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText=(TextView)view.findViewById(R.id.title_text);
        backButton=(Button)view.findViewById(R.id.back_button);
        listView=(ListView)view.findViewById(R.id.list_view);


        adapter=new ArrayAdapter<>(getActivity().getApplicationContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String weatherId = cityList.get(position).getId();
                if (getActivity() instanceof WeatherMainActivity) {
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                } else if (getActivity() instanceof WeatherActivity) {
                    WeatherActivity activity = (WeatherActivity) getActivity();
                    activity.drawerLayout.closeDrawers();
                    activity.swipeRefresh.setRefreshing(true);
                    activity.requestWeather(weatherId);
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "嘿嘿", Toast.LENGTH_SHORT).show();
            }
        });
    }
}