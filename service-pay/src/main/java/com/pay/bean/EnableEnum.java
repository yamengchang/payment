package com.pay.bean;

/**
 * Created by zwj on 2018/10/26.
 */
public enum EnableEnum {
    Enabling("0"),//激活中
    Enabled("1"),//已激活
    UnEnabled("2");//激活失败
    private String code;
    EnableEnum(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
