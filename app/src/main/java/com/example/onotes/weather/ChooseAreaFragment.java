package com.example.onotes.weather;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.onotes.App;
import com.example.onotes.R;
import com.example.onotes.bean.City;
import com.example.onotes.datebase.CityDbHelper;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.SharedPreferenesUtil;
import com.example.onotes.view.SideBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by cwj on 2017/3/9 13:52
 */
public class ChooseAreaFragment extends Fragment{

    private EditText searchText;
    private SearchView mSearchView;
    private ListView listView;


    public static String[] INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    public static int[]indexposition=new int[26];

    private SideBar mSideBar;



    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    private List<String> fitdataList = new ArrayList<>();


    private TextView dialog;

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

    public  void search() {
        dataList.clear();
        CityDbHelper cityDbHelper = new CityDbHelper(getActivity());
        SQLiteDatabase db = cityDbHelper.getWritableDatabase();
        Log.d("db", "search");
        Cursor cursor = db.query("City", null, null, null, null, null, "CityEn ASC");

        int j = 0;
        int i=0;

        int index=0;
        String language=SharedPreferenesUtil.getLanguage();

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
                city.setSortLetters(CityEn.substring(0,1).toUpperCase());
                city.setType(1);
                cityList.add(city);


                if(i==0){
                    City type=new City();
                    type.setType(0);
                    type.setSortLetters(cityList.get(0).getSortLetters());
                    //type.setCityZh("aa");

                   // cityList.add(type);

                    dataList.add(type.getSortLetters());


                    indexposition[index++]=dataList.size()-1;
                    LogUtil.d("whynot","indexposition "+ type.getSortLetters()+indexposition[index-1]);

                }else if(!cityList.get(i-1).getSortLetters().equals(cityList.get(i).getSortLetters())){


                    while(index<=24&& !cityList.get(i).getSortLetters().equals(INDEX_STRING[index])){
                        /*if(cityList.get(i).getSortLetters().eqINDEX_STRING[index]){
                            LogUtil.d("whynot","wocai");
                            break;
                        }*/
                        LogUtil.d("whynot",cityList.get(i).getSortLetters()+" "+INDEX_STRING[index]);
                        indexposition[index++]=-1;
                        LogUtil.d("whynot","add");
                    }

                    City type=new City();

                    type.setType(0);
                    type.setSortLetters(cityList.get(i).getSortLetters());
                  //  type.setCityZh("aa");

                    //cityList.add(type);
                    dataList.add(type.getSortLetters());

                    indexposition[index++]=dataList.size()-1;

                    LogUtil.d("whynot","indexposition "+ type.getSortLetters()+indexposition[index-1]);
                }


                i++;
                LogUtil.d("sortletter",city.getSortLetters());

                if(language.equals("en"))
                {
                    //String cityEn=cityList.get(j).getCityEn();
                    //dataList.add(cityEn.substring(0,1).toUpperCase()+cityEn.substring(1,cityEn.length()-1));
                    dataList.add(cityList.get(j).getCityEn());
                }else {
                    dataList.add(cityList.get(j).getCityZh());
                }


                adapter.notifyDataSetChanged();
                j++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

       /* for(int k=0;k<26;k++)
            indexposition[k]
        }*/
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
        listView = (ListView) view.findViewById(R.id.list_view);

        mSearchView=(SearchView) view.findViewById(R.id.searchView);

        mSideBar = (SideBar)view.findViewById(R.id.sidebar);
        dialog = (TextView) view.findViewById(R.id.dialog);

        mSideBar.setTextView(dialog);

        adapter = new ArrayAdapter<>(getActivity().getApplication(), android.R.layout.simple_list_item_1, dataList);


        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(TextUtils.isEmpty(query)){
                    adapter.clear();

                    adapter = new ArrayAdapter<>(getActivity().getApplication(), android.R.layout.simple_list_item_1, dataList);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                }
             /*   for (int i = 0; i < cityList.size(); i++) {
                    //   if (searchText.getText().toString().equals(cityList.get(i).getCityZh())) {
                    if (cityList.get(i).getCityZh().contains(searchText.getText().toString())) {
                        fitdataList.add(cityList.get(i).getCityZh());
                        //dataList.add(cityList.get(i).getCityZh());
                        //Toast.makeText(getActivity(), cityList.get(i).getCityZh(), Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }*/
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)){
                    adapter.clear();
                    adapter=null;
                    adapter = new ArrayAdapter<>(getActivity().getApplication(), android.R.layout.simple_list_item_1, dataList);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                }
                else {
                    fitdataList.clear();
                    for (int i = 0; i < cityList.size(); i++) {
                        if( cityList.get(i).getCityZh().contains(newText)){
                            fitdataList.add(cityList.get(i).getCityZh());
                            LogUtil.d("why",cityList.get(i).getCityZh());
                        }

                    }
                    adapter.clear();
                    adapter=null;
                    adapter = new ArrayAdapter<>(getActivity().getApplication(), android.R.layout.simple_list_item_1, fitdataList);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                }


                return true;
            }
        });

        //filter space
       /* searchText.addTextChangedListener(new TextWatcher() {
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
                 //   if (searchText.getText().toString().equals(cityList.get(i).getCityZh())) {
                    if (cityList.get(i).getCityZh().contains(searchText.getText().toString())) {
                        fitdataList.add(cityList.get(i).getCityZh());
                        //dataList.add(cityList.get(i).getCityZh());
                        Toast.makeText(getActivity(), cityList.get(i).getCityZh(), Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }

            }
        });*/

        //mSearchView=(SearchView)view.findViewById(R.id.searchView);



        listView.setAdapter(adapter);


        return view;
    }

public void update(String s){
    for(int i=0;i<26;i++){
        if(s.equals(INDEX_STRING[i])){
            if(indexposition[i]==-1)return;
            LogUtil.d("whynot","equal");
            int position=indexposition[i];
            LogUtil.d("index_string",INDEX_STRING[i]);
            listView.setSelection(position );
        }
        //listView.setSelection(position + 1);

    }
}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("db", "onActivityCreated");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("db", "onActivityCreated");
                int realposition=1;
                for(int i=0;i<26;i++){
                    if(i<25) {
                        if(indexposition[i]==position||indexposition[i+1]==position)return;

                        if(indexposition[i]<position&&indexposition[i+1]>position){
                            realposition=position-i-1;
                            break;
                        }
                    }else{

                        realposition=position-i-1;
                        break;
                    }
                }
                String weatherId = "";
                if (fitdataList.size() == 1&&fitdataList.get(0).length()!=1) {
                    for (int i = 0; i < cityList.size(); i++) {

                        if (fitdataList.get(position).equals(cityList.get(i).getCityZh())) {
                            weatherId = cityList.get(i).getId();
                            break;
                        }
                    }
                } else {
                   // weatherId = cityList.get(position).getId();
                    LogUtil.d("index",cityList.size()+" "+realposition);

                    weatherId = cityList.get(realposition).getId();
                }

                     LogUtil.d("shared", "id:"+weatherId);
                    SharedPreferenesUtil.setWeatherid(weatherId);
                    LogUtil.d("shared", SharedPreferenesUtil.getWeatherid()+" sasdfdf");

                    SharedPreferenesUtil.setCityEn(cityList.get(realposition).getCityEn());
                    SharedPreferenesUtil.setCityZh(cityList.get(realposition).getCityZh());


                if (getActivity() instanceof WeatherMainActivity) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), WeatherActivity.class);
                    //intent.putExtra("weather_id", weatherId);
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        search();
                    }
                });

       // adapter = new WeatherAdapter(getActivity().getApplication(), cityList);
      //  listView.setAdapter(adapter);
       // adapter.notifyDataSetChanged();
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


}
