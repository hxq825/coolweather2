package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 2017/2/21.
 * 天气 服务器 里面now对象
 *  "now":{
 *      "tmp":"29",
 *      "cond":{
 *      "txt":"阵雨"}
 *  }
 *
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more ;

    public class More{
        @SerializedName("txt")
        public String info;
    }

}
