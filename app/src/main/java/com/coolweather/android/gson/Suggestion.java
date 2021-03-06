package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by User on 2017/2/21.
 *
 * 天气json Suggestion
 *
 * "suggestion":{
 *      "comf":{
 *          "txt":"天气，。。。。"
 *      }，
 *       "cw":{
 *          "txt":"天气，。。下雨。。"
 *      }，
 *       "sport":{
 *          "txt":"天气，。天晴。。。"
 *      }
 *      }
 */

public class Suggestion {

    public Air air;

    public class Air{

        String brf;
        String txt;
    }
    
    @SerializedName("comf")
    public Comfort mComfort;

    @SerializedName("cw")
    public ComWash mComWash;

    public Sport sport;

    public class Comfort{
        public String brf;
        @SerializedName("txt")
        public String info;
    }

    public class ComWash{
        public String brf;
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        public String brf;
        @SerializedName("txt")
        public String info;
    }


}
