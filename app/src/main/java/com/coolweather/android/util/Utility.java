package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Created by User on 2017/2/20.
 *
 * 解析访问网络获取 返回的数据
 *
 */

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     *
     * @param response
     * @return
     */
    public static boolean handleProviceResponse(String response){
    if (!TextUtils.isEmpty(response)){
        try {
            JSONArray allProvinces = new JSONArray(response);
            for (int i =0 ;i<allProvinces.length();i++){
                JSONObject proviceObject =allProvinces.getJSONObject(i);
                Province province =new Province();
                province.setProvinceName(proviceObject.getString("name"));
                province.setProvinceCode(proviceObject.getInt("id"));
                province.save();// 保存

        return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    return false;
}

    /**
     * 解析 服务器返回的市级数据
     * @param response
     * @param provinceId        上级id
     * @return
     */
    public static boolean handleCityResponse(String response,int provinceId){

        try {
            JSONArray jsonCityArray =new JSONArray(response);
            for (int i =0;i<jsonCityArray.length();i++){
                JSONObject cityObject = jsonCityArray.getJSONObject(i);
                City city =new City();
                city.setCityName(cityObject.getString("name"));
                city.setCityCode(cityObject.getInt("id"));
                city.setProvinceId(provinceId);
                city.save();
                return true;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return false;
    }
    /**
     *
     * 解析县级服务器 返回的数据
     *
     */

    public static boolean handleCountyResponse(String response,int cityId){

        try {
            JSONArray jsonCountyArray = new JSONArray(response);
            for (int i=0; i<jsonCountyArray.length();i++){
                JSONObject jsonCountyObject = jsonCountyArray.getJSONObject(i);
                County county =new County();
                county.setCountyName(jsonCountyObject.getString("name"));
                county.setWeatherId(jsonCountyObject.getString("weather_id"));
                county.setCityId(cityId);
                county.save(); //保存
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }




}
