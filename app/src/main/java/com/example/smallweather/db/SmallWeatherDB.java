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
    * 数据库名
    * */
    public static final String DB_NAME = "small_weather";

    /*
    * 数据库版本
    * */
    public static final int VERSION = 1;

    /*
    * 将构造方法私有化
    * */
    private SmallWeatherDB(Context context){
        SmallWeatherOpenHelper helper = new SmallWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = helper.getWritableDatabase();
    }

    /*
    * 获取smallweather的实例
    * */
    public synchronized static SmallWeatherDB getInstance(Context context){
        if(smallWeatherDB ==null){
            smallWeatherDB = new SmallWeatherDB(context);
        }
        return smallWeatherDB;
    }

    /*
    * 将province实例存储到数据库
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
    * 从数据库中读取全国所有的省份信息
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
    * 将city实例存储到数据库中
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
    * 从数据库中读取某省下所有城市的信息
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
    * 将country实例存储到数据库中
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
    * 从数据库中读取某市下所有县城信息
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
