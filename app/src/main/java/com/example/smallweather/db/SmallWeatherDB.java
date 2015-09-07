package com.example.smallweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.smallweather.model.City;
import com.example.smallweather.model.Country;
import com.example.smallweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lhf on 2015/9/6.
 */
public class SmallWeatherDB {
    private static SmallWeatherDB smallWeatherDB;
    private SQLiteDatabase db;

    /*
    * ���ݿ���
    * */
    public static final String DB_NAME = "small_weather";

    /*
    * ���ݿ�汾
    * */
    public static final int VERSION = 1;

    /*
    * �����췽��˽�л�
    * */
    private SmallWeatherDB(Context context){
        SmallWeatherOpenHelper helper = new SmallWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = helper.getWritableDatabase();
    }

    /*
    * ��ȡsmallweather��ʵ��
    * */
    public synchronized static SmallWeatherDB getInstance(Context context){
        if(smallWeatherDB ==null){
            smallWeatherDB = new SmallWeatherDB(context);
        }
        return smallWeatherDB;
    }

    /*
    * ��provinceʵ���洢�����ݿ�
    * */
    public void saveProvince(Province province){
        if(province != null){
            ContentValues values = new ContentValues();
            values.put("province_code",province.getProviceCode());
            values.put("province_name",province.getProviceName());
            db.insert("province",null,values);
        }
    }

    /*
    * �����ݿ��ж�ȡȫ�����е�ʡ����Ϣ
    * */
    public List<Province> loadProvince(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProviceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                province.setProviceName(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province);
            }while(cursor.moveToNext());
        }
        return list;
    }

    /*
    * ��cityʵ���洢�����ݿ���
    * */
    public void saveCity(City city){
        if(city != null){
            ContentValues values = new ContentValues();
            values.put("city_code",city.getCityCode());
            values.put("city_name",city.getCityName());
            values.put("province_id",city.getProvinceId());
            db.insert("city",null,values);
        }
    }

    /*
    * �����ݿ��ж�ȡĳʡ�����г��е���Ϣ
    * */
    public List<City> loadCity(int proviceId){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("city",null,"province_id = ?",new String[]{String.valueOf(proviceId)},null,null,null);
        if(cursor.moveToFirst()){
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(proviceId);
                list.add(city);
            }while (cursor.moveToNext());
        }
        return list;
    }

    /*
    * ��countryʵ���洢�����ݿ���
    * */
    public void saveCountry(Country country){
        if(country != null){
            ContentValues values = new ContentValues();
            values.put("country_code",country.getCountryCode());
            values.put("country_name",country.getCountryName());
            values.put("city_id",country.getCityId());
            db.insert("country",null,values);
        }
    }

    /*
    * �����ݿ��ж�ȡĳ���������س���Ϣ
    * */
    public List<Country> loadCountry(int cityId){
        List<Country> list = new ArrayList<Country>();
        Cursor cursor = db.query("country",null,"city_id = ?",new String[]{String.valueOf(cityId)},null,null,null);

        if(cursor.moveToFirst()){
            do{
                Country country = new Country();
                country.setId(cursor.getInt(cursor.getColumnIndex("id")));
                country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
                country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
                country.setCityId(cityId);
                list.add(country);
            }while(cursor.moveToNext());
        }
        return list;
    }



}
