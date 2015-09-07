package com.example.smallweather.util;

import android.text.TextUtils;

import com.example.smallweather.model.City;
import com.example.smallweather.model.Country;
import com.example.smallweather.model.Province;
import com.example.smallweather.db.SmallWeatherDB;

/**
 * Created by lhf on 2015/9/6.
 */
public class Utility {

    /*
    * 解析和处理服务器返回的省级数据
    * */
    public synchronized static boolean handleProvincesResponse(SmallWeatherDB smallWeatherDB,String response){
       if(!TextUtils.isEmpty(response)){
           String [] allProvince = response.split(",");
           if(allProvince != null&&allProvince.length>0){
               for(String p :allProvince){
                   String [] array = p.split("\\|");
                   Province province = new Province();
                   province.setProviceCode(array[0]);
                   province.setProviceName(array[1]);
                   //将解析出来的数据存储到province表中
                   smallWeatherDB.saveProvince(province);
               }
               return true;
           }

       }
        return false;
    }

    /*
    * 解析和处理服务器返回的市级数据
    * */
    public static boolean handleCityResponse(SmallWeatherDB smallWeatherDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String [] allCities = response.split(",");
            if(allCities != null&&allCities.length>0){
                for(String c :allCities){
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    smallWeatherDB.saveCity(city);
                }
                return true;
            }

        }
        return false;
    }

    /*
    * 解析和处理服务器返回的县级数据
    * */
    public static boolean handleCountryResponse(SmallWeatherDB smallWeatherDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String [] allCountries = response.split(",");
            if(allCountries != null && allCountries.length>0){
                for(String co : allCountries){
                    String [] array = co.split("\\|");
                    Country country = new Country();
                    country.setCityId(cityId);
                    country.setCountryCode(array[0]);
                    country.setCountryName(array[1]);
                    smallWeatherDB.saveCountry(country);
                }
                return true;
            }

        }

        return false;
    }


}
