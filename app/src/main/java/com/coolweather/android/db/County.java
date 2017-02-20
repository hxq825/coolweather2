package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by User on 2017/2/20.
 *
 * 县城实体类  模型
 * 继承一个封装好的数据库框架
 */

public class County extends DataSupport{

    private int id ;
    private String countyName ;
    private String weatherId;//因为县是最后一级 所以没有下面的编码了，而是天气id，因为天气以县定位最小单位
    private int cityId;//记录所属市上一级id值

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
