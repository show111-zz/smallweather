package com.example.smallweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smallweather.R;
import com.example.smallweather.util.HttpCallbackListener;
import com.example.smallweather.util.HttpUtil;
import com.example.smallweather.util.Utility;

/**
 * Created by lhf on 2015/9/7.
 */
public class WeahterActivity extends Activity implements View.OnClickListener{

    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDataText;

    private Button switchCity;
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDataText = (TextView) findViewById(R.id.current_date);

        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);

        String countryCode = getIntent().getStringExtra("country_code");
        if(!TextUtils.isEmpty(countryCode)){
            // 有县级代号时就去查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countryCode);
        }else{
            // 没有县级代号时就直接显示本地天气
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上。
     */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        publishText.setText("今天"+prefs.getString("publish_time","")+"发布");
        currentDataText.setText(prefs.getString("current_date",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
//        Intent intent = new Intent(this,AutoUpdateService.class);
//        startActivity(intent);
    }

    /**
     * 查询县级代号所对应的天气代号。
     */
    private void queryWeatherCode(String countryCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countryCode + ".xml";
        queryFromServer(address,"countryCode");
    }

    /**
     * 查询天气代号所对应的天气。
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address,"weatherCode");
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(WeahterActivity.this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("正在同步...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息。
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if("countryCode".equals(type)){
                    if(!TextUtils.isEmpty(response)){
                        // 从服务器返回的数据中解析出天气代号
                        String [] array = response.split("\\|");
                        if(array != null && array.length == 2){
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                }else if("weatherCode".equals(type)){
                    // 处理服务器返回的天气信息
                    Utility.handleWeatherResponse(WeahterActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

}
