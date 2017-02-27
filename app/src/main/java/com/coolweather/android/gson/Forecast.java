package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 2017/2/21.
 *
 *      天气里json数据 daily_forecast
 *      "daily_forecast":[{
 *          "date":"2016-08-09",
 *          "cond":{
 *              "txt_d":"阵雨"
 *          }
 *          "tmp":{
 *              "max":"34",
 *              "min":"27"
 *          }
 *      },       "date":"2016-08-08",
 *          "cond":{
 *              "txt_d":"阵雨"
 *          }
 *          "tmp":{
 *              "max":"34",
 *              "min":"27"
 *          }
 *      }]]
 *
 */

public class Forecast {

    public String date;

    @SerializedName("tmp")
    public Temperature mTemperature;

    @SerializedName("cond")
    public More more;

    public class Temperature {
        public String max;

        public String min;

    }

    public  class More{
        @SerializedName("txt_d")
        public String info;
    }
}
