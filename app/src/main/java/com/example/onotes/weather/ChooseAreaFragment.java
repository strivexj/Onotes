package com.example.onotes.weather;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.example.onotes.R;
import com.example.onotes.bean.City;
import com.example.onotes.datebase.CityDbHelper;
import com.example.onotes.utils.LogUtil;
import com.example.onotes.utils.SharedPreferenesUtil;
import com.example.onotes.view.SideBar;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by cwj on 2017/3/9 13:52
 */
public class ChooseAreaFragment extends Fragment{


    private SearchView mSearchView;

    private ListView listView;


    public static String[] INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};
    //可能存在的索引
    public static int[]indexposition=new int[26];

    //真实存在的索引
    public static int[]realIndex=new int[26];

    private SideBar mSideBar;


    private List<String> dataList = new ArrayList<>();

    private List<String> fildataList = new ArrayList<>();

    private TextView dialog;

    private List<City> cityList = new ArrayList<>();

    private boolean isfilter=false;

    private List<City> filcityList = new ArrayList<>();

    private ArrayAdapter<String> filadapter;

    private ArrayAdapter<String> adapter;
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

        listView.setAdapter(adapter);

        CityDbHelper cityDbHelper = new CityDbHelper(getActivity());
        SQLiteDatabase db = cityDbHelper.getWritableDatabase();
        Log.d("bbb", "search");
        Cursor cursor = db.query("City", null, null, null, null, null, "CityEn ASC");

        int j = 0;
        int i=0;

        int index=0;

        String language=SharedPreferenesUtil.getLanguage();

        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                String cityId = cursor.getString(cursor.getColumnIndex("cityid"));
                String CityEn = cursor.getString(cursor.getColumnIndex("cityEn"));
                String CityZh = cursor.getString(cursor.getColumnIndex("cityZh"));
                String provinceEn = cursor.getString(cursor.getColumnIndex("provinceEn"));
                String provinceZh = cursor.getString(cursor.getColumnIndex("provinceZh"));
                String leaderEn = cursor.getString(cursor.getColumnIndex("leaderEn"));
                String leaderZh = cursor.getString(cursor.getColumnIndex("leaderZh"));
                String lat = cursor.getString(cursor.getColumnIndex("lat"));
                String lon = cursor.getString(cursor.getColumnIndex("lon"));


                city.setId(cityId);
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
                    dataList.add(type.getSortLetters());

                    indexposition[index++]=dataList.size()-1;

                    LogUtil.d("whynot","indexposition "+ type.getSortLetters()+indexposition[index-1]);

                }else if(!cityList.get(i-1).getSortLetters().equals(cityList.get(i).getSortLetters())){

                    while(index<=24&& !cityList.get(i).getSortLetters().equals(INDEX_STRING[index])){

                        LogUtil.d("whynot",cityList.get(i).getSortLetters()+" "+INDEX_STRING[index]);
                        indexposition[index++]=-1;
                        LogUtil.d("whynot","add");
                    }

                    City type=new City();

                    type.setType(0);
                    type.setSortLetters(cityList.get(i).getSortLetters());

                    dataList.add(type.getSortLetters());

                    indexposition[index++]=dataList.size()-1;

                    LogUtil.d("whynot","indexposition "+ type.getSortLetters()+indexposition[index-1]);
                }

                i++;
                LogUtil.d("sortletter",city.getSortLetters());

                if(language.equals("en"))
                {
                    //如果出现同名的城市，加上省份名
                    if(j>1&&dataList.get(dataList.size()-1).contains(cityList.get(j).getCityEn())){
                        dataList.remove(dataList.size()-1);
                        dataList.add(cityList.get(j-1).getCityEn()+" , "+cityList.get(j-1).getProvinceEn());
                        dataList.add(cityList.get(j).getCityEn()+" , "+cityList.get(j).getProvinceEn());
                        LogUtil.d("same",dataList.get(dataList.size()-1));
                    }

                    else {
                        dataList.add(cityList.get(j).getCityEn());
                    }
                }else {

                    if(j>1&&dataList.get(dataList.size()-1).contains(cityList.get(j).getCityZh())){
                        dataList.remove(dataList.size()-1);
                        dataList.add(cityList.get(j-1).getCityZh()+" , "+cityList.get(j-1).getProvinceZh());
                        dataList.add(cityList.get(j).getCityZh()+" , "+cityList.get(j).getProvinceZh());
                        LogUtil.d("same",dataList.get(dataList.size()-1));
                    }else {
                        dataList.add(cityList.get(j).getCityZh());
                    }

                }



                adapter.notifyDataSetChanged();
                j++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }


    @Override
    public void onAttach(Context context) {
        Log.d("bbb", "onattach");
        super.onAttach(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        Log.d("bbb", "oncreateview");


        listView = (ListView) view.findViewById(R.id.list_view);

        mSearchView=(SearchView) view.findViewById(R.id.searchView);

        mSideBar = (SideBar)view.findViewById(R.id.sidebar);
        dialog = (TextView) view.findViewById(R.id.dialog);

        mSideBar.setTextView(dialog);




        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    filcityList.clear();
                    fildataList.clear();

                if(newText.equals("")){
                    LogUtil.d("eee","empty");

                    listView.setAdapter(adapter);

                    adapter.notifyDataSetChanged();

                    isfilter=false;

                }else {


                    for (int i = 0; i < cityList.size(); i++) {
                        if( cityList.get(i).getCityZh().contains(newText)){

                            filcityList.add(cityList.get(i));

                            fildataList.add(cityList.get(i).getCityZh());

                            LogUtil.d("rrr",cityList.get(i).getCityZh());

                        }else if(cityList.get(i).getCityEn().toLowerCase().contains(newText)){
                            filcityList.add(cityList.get(i));
                            fildataList.add(cityList.get(i).getCityEn());
                        }

                    }

                //出现同名就加上省份
                    for (int i = 1; i < fildataList.size(); i++) {

                        if(fildataList.get(i-1).contains(fildataList.get(i))){

                                fildataList.remove(i-1);

                                fildataList.add(filcityList.get(i-1).getCityEn()+" , "+filcityList.get(i-1).getProvinceEn());
                                fildataList.add(filcityList.get(i).getCityEn()+" , "+filcityList.get(i).getProvinceEn());

                                LogUtil.d("same",dataList.get(dataList.size()-1));
                            }
                        }


                    listView.setAdapter(filadapter);
                    filadapter.notifyDataSetChanged();

                    isfilter=true;
                }
                return true;
            }
        });



        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {

                 update(s);


            }
        });


       // adapter= new ArrayAdapter<>(getActivity().getApplication(), android.R.layout.simple_list_item_1, dataList);
        adapter= new ArrayAdapter<>(getActivity().getApplication(), R.layout.item_weather, dataList);

       /* 夜间模式ltextview的字体颜色不会跟着改变。。。待改。。
       View v = LayoutInflater.from(getActivity()).inflate(R.layout.item_weather, null);

        TextView textView=(TextView)v.findViewById(R.id.weather_text);
        textView.setTextColor(getResources().getColor(R.color.textcolor));*/

        listView.setAdapter(adapter);

        filadapter = new ArrayAdapter<>(getActivity().getApplication(),  R.layout.item_weather, fildataList);

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
    }

}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("bbb", "onActivityCreated");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("db", "onActivityCreated");
                String weatherId = "";

                if(!isfilter){

                    int realposition=1;

                    int withoutIndexNumber=0;

                    for(int i=0;i<26;i++){
                        if(indexposition[i]==-1){
                            withoutIndexNumber++;
                        }

                        if(i<25) {
                            if(indexposition[i]==position||indexposition[i+1]==position)return;

                            if(indexposition[i]<position&&indexposition[i+1]>position){
                                realposition=position-i-1+withoutIndexNumber;
                                break;
                            }
                        }else{
                            realposition=position-i-1+withoutIndexNumber;
                            break;
                        }
                    }

                        LogUtil.d("index",cityList.size()+" "+realposition);

                        weatherId = cityList.get(realposition).getId();

                    LogUtil.d("shared", "id:"+weatherId);

                    LogUtil.d("shared", SharedPreferenesUtil.getWeatherid()+" sasdfdf");

                    SharedPreferenesUtil.setCityEn(cityList.get(realposition).getCityEn());
                    SharedPreferenesUtil.setCityZh(cityList.get(realposition).getCityZh());

                }else {

                    weatherId=filcityList.get(position).getId();
                    SharedPreferenesUtil.setCityEn(filcityList.get(position).getCityEn());
                    SharedPreferenesUtil.setCityZh(filcityList.get(position).getCityZh());
                }

                SharedPreferenesUtil.setWeatherid(weatherId);

                if (getActivity() instanceof WeatherMainActivity) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), WeatherActivity.class);
                    //intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                   // isfilter=false;
                    Log.d("refresh", "start ");
                } else if (getActivity() instanceof WeatherActivity) {
                    WeatherActivity activity = (WeatherActivity) getActivity();
                    activity.drawerLayout.closeDrawers();
                   // isfilter=false;
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
        Log.d("bbb", "onstart");
    }

    @Override
    public void onResume() {
        Log.d("bbb", "onresume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d("bbb", "onpause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d("bbb", "onstop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {

        Log.d("bbb", "onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.d("bbb", "onDestroy");
        super.onDestroy();
    }


}
