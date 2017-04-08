package com.example.onotes.weather;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.Toast;


import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.adapter.WeatherAdapter;
import com.example.onotes.bean.City;
import com.example.onotes.datebase.CityDbHelper;
import com.example.onotes.utils.HttpUtil;
import com.example.onotes.utils.PinyinUtils;
import com.example.onotes.utils.WeatherUtil;
import com.example.onotes.view.PinyinComparator;
import com.example.onotes.view.SideBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.onotes.R.id.dialog;

/**
 * Created by cwj on 2017/3/9 13:52
 */
public class ChooseAreaFragment extends Fragment {
    private ProgressDialog progressDialog;
    private EditText searchText;
    private Button backButton;
    private SearchView mSearchView;
    private ListView listView;
    private WeatherAdapter adapter;

    private SideBar sideBar;

   //1. private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();


    /**
     * city list
     */
    private List<City> cityList = new ArrayList<>();


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

    private void search() {
        dataList.clear();
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
                city.setSortLetters(CityEn.charAt(0)+"");
                cityList.add(city);
                Log.d("db", "find a city  " + j);
                dataList.add(cityList.get(j).getCityZh());
                //5.adapter.notifyDataSetChanged();
                j++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }


    @Override
    public void onAttach(Context context) {
        Log.d("db", "onattach");
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        Log.d("db", "oncreateview");
        searchText = (EditText) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);

        //filter space
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("search", "" + cityList.size());
                for (int i = 0; i < cityList.size(); i++) {
                    if (searchText.getText().toString().equals(cityList.get(i).getCityZh())) {
                        dataList.clear();
                        dataList.add(cityList.get(i).getCityZh());
                        //6.wadapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        });
        //mSearchView=(SearchView)view.findViewById(R.id.searchView);

       //2. adapter = new ArrayAdapter<>(getActivity().getApplication(), android.R.layout.simple_list_item_1, dataList);

        //3.listView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("db", "onActivityCreated");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("db", "onActivityCreated");
                String weatherId = "";
                if (dataList.size() == 1) {
                    for (int i = 0; i < cityList.size(); i++) {
                        if (dataList.get(position).equals(cityList.get(i).getCityZh())) {
                            weatherId = cityList.get(i).getId();
                            break;
                        }
                    }
                } else {
                    weatherId = cityList.get(position).getId();
                }

                SharedPreferences.Editor editor = App.getContext().getSharedPreferences("weather",MODE_PRIVATE).edit();
                editor.putString("weatherid", weatherId);
                editor.apply();
                if (getActivity() instanceof WeatherMainActivity) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), WeatherActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                    Log.d("refresh", "start ");
                } else if (getActivity() instanceof WeatherActivity) {
                    WeatherActivity activity = (WeatherActivity) getActivity();
                    activity.drawerLayout.closeDrawers();
                    activity.swipeRefresh.setRefreshing(true);
                    Log.d("refresh", "instanceof ");
                    activity.requestWeather(weatherId);

                }
            }
        });


        search();
        adapter = new WeatherAdapter(getActivity().getApplication(), cityList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("db", "onstart");
    }

    @Override
    public void onResume() {
        Log.d("db", "onresume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("db", "onpause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d("db", "onstop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {

        Log.d("db", "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("db", "onDestroy");
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
