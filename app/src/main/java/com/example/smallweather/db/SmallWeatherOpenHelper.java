package com.example.smallweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lhf on 2015/9/6.
 */
public class SmallWeatherOpenHelper extends SQLiteOpenHelper{
    /**
     *  Province表建表语句
     */
    public static final String CREATE_PROVINCE = "create table province(id integer primary key autoIncrement,province_code text,province_name text)";

    /**
     *  city表建表语句
     */
    public static final String CREATE_CITY = "create table city(id integer primary key autoincrement,city_code text,city_name text,province_id integer)";

    /**
     *  country表建表语句
    */
    public static final String CREATE_COUNTRY = "create table country(id integer primary key autoincrement,country_code text,country_name text,city_id integer)";


    public SmallWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
        sqLiteDatabase.execSQL(CREATE_CITY);
        sqLiteDatabase.execSQL(CREATE_COUNTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
