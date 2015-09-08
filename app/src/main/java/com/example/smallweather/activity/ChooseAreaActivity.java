package com.example.smallweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smallweather.R;
import com.example.smallweather.model.City;
import com.example.smallweather.model.Country;
import com.example.smallweather.model.Province;
import com.example.smallweather.db.SmallWeatherDB;
import com.example.smallweather.util.HttpCallbackListener;
import com.example.smallweather.util.HttpUtil;
import com.example.smallweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lhf on 2015/9/6.
 */
public class ChooseAreaActivity extends Activity{

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;

    private TextView titleText;
    private ListView listView;

    private ProgressDialog dialog;
    private SmallWeatherDB smallWeatherDB;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> provinceList;//省列表
    private List<City> cityList;//市列表
    private List<Country> countryList;//县列表

    private Province selectedProvince;//选中的省份
    private City selectedCity;//选中的城市
    private Country selectedCountry;//选中的城市

    private int currentLevel;//当前选中的级别
    /**
     * 是否从WeatherActivity中跳转过来。
     */
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("city_selected",false)&&!isFromWeatherActivity){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        titleText = (TextView) findViewById(R.id.title_tv);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        smallWeatherDB = SmallWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(i);
                    queryCity();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCountry();
                }else if(currentLevel == LEVEL_COUNTRY){
                    String countryCode = countryList.get(i).getCountryCode();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("country_code",countryCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        provinceList = smallWeatherDB.loadProvince();
        if(provinceList.size() > 0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getProviceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"province");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    public void queryCity(){
        cityList = smallWeatherDB.loadCity(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProviceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProviceCode(),"city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    public void queryCountry(){
        countryList = smallWeatherDB.loadCountry(selectedCity.getId());
        if(countryList.size()>0){
            dataList.clear();
            for(Country country : countryList){
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTRY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"country");
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县数据。
     */
    public void queryFromServer(final String code,final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+ code +".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvincesResponse(smallWeatherDB,response);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(smallWeatherDB,response,selectedProvince.getId());
                }else if("country".equals(type)){
                    result = Utility.handleCountryResponse(smallWeatherDB,response,selectedCity.getId());
                }

                if(result){
                    // 通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCity();
                            }else if("country".equals(type)){
                                queryCountry();
                            }
                        }
                    });

                }
            }

            @Override
            public void onError(Exception e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }

    private void showProgressDialog() {
        if(dialog == null){
            dialog = new ProgressDialog(this);
            dialog.setMessage("正在加载...");
            dialog.setCanceledOnTouchOutside(false);
        }
        dialog.show();

    }
    private void closeProgressDialog() {
        if(dialog != null){
            dialog.dismiss();
        }
    }

    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出。
     */
    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTRY){
            queryCity();
        }else if(currentLevel == LEVEL_CITY){
            queryProvinces();
        }else{
            if(isFromWeatherActivity){
                Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
