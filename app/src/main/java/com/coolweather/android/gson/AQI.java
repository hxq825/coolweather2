package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 2017/2/21.
 *
 * 天气json里面的AQI对象 数据类
 *
 *      "aqi":{
 *          city:{
 *              "aqi":"44",
 *              "pm25":"13"
 *          }
 *      }
 */

public class AQI {

    @SerializedName("city")
    public AQICity mCity;

    public class AQICity{
        public String aqi;
        public String pm10;
        public String pm25;
        public String qlty;


    }

}
