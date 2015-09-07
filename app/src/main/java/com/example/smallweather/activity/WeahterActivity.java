package com.example.smallweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smallweather.R;

/**
 * Created by lhf on 2015/9/7.
 */
public class WeahterActivity extends Activity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;//��������
    private TextView publishText;//����ʱ��
    private TextView weatherDespText;//����������Ϣ
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDataText;//��ǰʱ��

    private Button switchCity;//ת�����а�ť
    private Button refreshWeather;//ˢ�°�ť

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
            //���ؼ����ž�ȥ��ѯ
            publishText.setText("ͬ����...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countryCode);
        }else{
            //û���ؼ����ž���ʾ����������Ϣ
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    private void showWeather() {

    }

    private void queryWeatherCode(String countryCode) {

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
                publishText.setText("ͬ����...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);



                break;
            default:
                break;
        }
    }

}
