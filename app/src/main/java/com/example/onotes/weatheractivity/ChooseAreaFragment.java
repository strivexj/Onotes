package com.example.onotes.weatheractivity;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by cwj on 2017/3/9 13:52
 */
public class ChooseAreaFragment extends Fragment{
    public static final int LEVEL_CITY=1;
    private ProgressDialog progressDialog;
    private EditText searchText;
    private Button backButton;
    private SearchView mSearchView;
    private ListView listView;
    private ArrayAdapter<String>adapter;
    private List<String>dataList=new ArrayList<>();



    /**
     * city list
     */
    private List<City>cityList=new ArrayList<>() ;


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
    public void queryCities(){
        Log.d("db","querycities");
        //titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.GONE);

        String address="https://cdn.heweather.com/china-city-list.json";
        SharedPreferences pref=getActivity().getSharedPreferences("account",MODE_PRIVATE);
        boolean isfirst=pref.getBoolean("isfirst",true);
        if(isfirst){
            queryFromServer(address);

            SharedPreferences.Editor editor = getActivity().getSharedPreferences("account", MODE_PRIVATE).edit();
            editor.putBoolean("isfirst",false);
            editor.apply();
        }
        search();
       // Log.d("db",""+cityList.size());
            Log.d("db","a");
            //int provinceCode=selectedProvince.getProvinceCode();
            //String address="http://guolin.tech/api/china/"+provinceCode;
    }

    private void search() {
        CityDbHelper cityDbHelper = new CityDbHelper(getActivity());
        SQLiteDatabase db = cityDbHelper.getWritableDatabase();
        Log.d("db", "search");
        Cursor cursor = db.query("City", null, null, null, null, null, "CityEn");
        int j = 0;
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                String setId = cursor.getString(cursor.getColumnIndex("cityid"));
                String CityEn = cursor.getString(cursor.getColumnIndex("cityEn"));
                String CityZh = cursor.getString(cursor.getColumnIndex("cityZh"));
                String provinceEn = cursor.getString(cursor.getColumnIndex("provinceEn"));
                String provinceZh = cursor.getString(cursor.getColumnIndex("provinceZh"));
                String leaderEn = cursor.getString(cursor.getColumnIndex("leaderEn"));
                String leaderZh = cursor.getString(cursor.getColumnIndex("leaderZh"));
                String lat = cursor.getString(cursor.getColumnIndex("lat"));
                String lon = cursor.getString(cursor.getColumnIndex("lon"));
                city.setId(setId);
                city.setCityEn(CityEn);
                city.setCityZh(CityZh);
                city.setProvinceEn(provinceEn);
                city.setProvinceZh(provinceZh);
                city.setLeaderEn(leaderEn);
                city.setLeaderZh(leaderZh);
                city.setLat(lat);
                city.setLon(lon);
                cityList.add(city);
                Log.d("db", "find a city  " + j);
                dataList.add(cityList.get(j).getCityZh());
                adapter.notifyDataSetChanged();
                j++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

       /* if (cityList.size() > 0) {
            dataList.clear();
            for (int i = 0; i < cityList.size(); i++) {
                // for(City city : cityList){
                dataList.add(cityList.get(i).getCityZh());
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                Log.d("db", "list add a city");
                //currentLevel=LEVEL_CITY;
            }
        }*/
    }
    /**
     * according to the address and type poured in,query data from server
     */
   // @TargetApi(23)
    private  void queryFromServer(String address){
        Log.d("db","queryFromServer");


        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //return main thread to handle logic through runOnUiThread()
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(),"loading failed",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result;
                 result=Utility.handleCityResponse(responseText,getActivity());
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                               queryCities();
                        }
                    });
                }
            }

        });
    }
    @Override
    public void onAttach(Context context) {
        Log.d("db","onattach");
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        Log.d("db","oncreateview");
        searchText=(EditText)view.findViewById(R.id.title_text);
        backButton=(Button)view.findViewById(R.id.back_button);
        listView=(ListView)view.findViewById(R.id.list_view);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("search",""+cityList.size());
                for(int i=0;i<cityList.size();i++){
                    if(searchText.getText().toString().equals(cityList.get(i).getCityZh())){
                        dataList.clear();
                        dataList.add(cityList.get(i).getCityZh());
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
        //mSearchView=(SearchView)view.findViewById(R.id.searchView);

        //queryCities();
        Log.d("db","fragment");
        adapter=new ArrayAdapter<>(getActivity().getApplication(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("db","onActivityCreated");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("db","onActivityCreated");
                String weatherId="";
            if(dataList.size()==1)
            {
                for(int i=0;i<cityList.size();i++) {
                    if (dataList.get(position).equals(cityList.get(i).getCityZh())) {
                        weatherId = cityList.get(i).getId();
                        break;
                    }
                }
            }
                else{
                weatherId = cityList.get(position).getId();
            }
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString("weatherid", weatherId);
                editor.apply();
               // String weatherId = cityList.get(poclsition).getId();
                if (getActivity() instanceof WeatherMainActivity) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), WeatherActivity.class);


                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                    Log.d("refresh","start ");
                } else if (getActivity() instanceof WeatherActivity) {
                    WeatherActivity activity = (WeatherActivity) getActivity();
                    activity.drawerLayout.closeDrawers();
                    activity.swipeRefresh.setRefreshing(true);
                    Log.d("refresh","instanceof ");
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

        queryCities();
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("db","onstart");
    }

    @Override
    public void onResume() {
        Log.d("db","onresume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("db","onpause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d("db","onstop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {

        Log.d("db","onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("db","onDestroy");
        super.onDestroy();
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
}
/*   city.setId(cursor.getString(cursor.getColumnIndex("cityid")));
                Log.d("db",cursor.getString(cursor.getColumnIndex("cityZh")));

                city.setCityEn(cursor.getString(cursor.getColumnIndex("CityEn")));
                Log.d("db",cursor.getString(cursor.getColumnIndex("cityEn")));

                city.setCityZh(cursor.getString(cursor.getColumnIndex("CityZh")));
                Log.d("db",cursor.getString(cursor.getColumnIndex("cityZh")));


                city.setProvinceEn(cursor.getString(cursor.getColumnIndex("provinceEn")));
                Log.d("db",cursor.getString(cursor.getColumnIndex("provinceEn")));


                city.setProvinceZh(cursor.getString(cursor.getColumnIndex("provinceZh")));
                Log.d("db",cursor.getString(cursor.getColumnIndex("provinceZh")));

                city.setLeaderEn(cursor.getString(cursor.getColumnIndex("leaderEn")));
                Log.d("db",cursor.getString(cursor.getColumnIndex("leaderEn")));


                city.setLeaderZh(cursor.getString(cursor.getColumnIndex("leaderZh")));
                Log.d("db",cursor.getString(cursor.getColumnIndex("leaderZh")));

                city.setLat(cursor.getString(cursor.getColumnIndex("lat")));
                Log.d("db",cursor.getString(cursor.getColumnIndex("lat")));

                city.setLon(cursor.getString(cursor.getColumnIndex("lon")));
                Log.d("db",cursor.getString(cursor.getColumnIndex("lon")));*/