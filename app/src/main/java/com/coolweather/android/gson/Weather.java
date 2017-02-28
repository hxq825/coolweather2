package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by User on 2017/2/21.
 *
 *      天气json总类
 */

public class Weather {
//天气数据 中还包含一项status数据，成功返回ok，失败则会返回具体原因，所以增加一个字段
    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    //      因为daily_forecast里面包含的是一个数组
    @SerializedName("daily_forecast")
    public List<Forecast> mForecasts;

}
