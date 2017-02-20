package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by User on 2017/2/20.
 */

public class Province extends DataSupport {

    private int id;
    private String proviceName;//省份名
    private int proviceCode ; //省份编码

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProviceName() {
        return proviceName;
    }

    public void setProviceName(String proviceName) {
        this.proviceName = proviceName;
    }

    public int getProviceCode() {
        return proviceCode;
    }

    public void setProviceCode(int proviceCode) {
        this.proviceCode = proviceCode;
    }
}
