package com.coolweather.android.gson;

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

    public AQICity mCity;

    public class AQICity{
        public String aqi;
        public String pm25;

    }

}
