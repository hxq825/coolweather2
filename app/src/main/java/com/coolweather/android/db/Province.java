package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by User on 2017/2/20.
 * 省实体
 * 继承已封装数据库
 */

public class Province extends DataSupport {

    private int id;
    private String provinceName;//省份名
    private int provinceCode ; //省份编码  以便查下一级数据

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
