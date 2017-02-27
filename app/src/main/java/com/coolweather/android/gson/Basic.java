package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 2017/2/21.
 *
 * 天气里面的basic对象
 * 对象包括city,id ，update:loc更新时间
 *
 *
 * 因为JSON中的一些字段可能不太适合直接作为java字段命名，因些用注解的方式来让json字段和
 * java字段之间建立映射关系
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;


    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }

}
