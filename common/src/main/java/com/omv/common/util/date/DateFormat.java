package com.omv.common.util.date;


/*
*@Author:Gavin
*@Email:gavinsjq@sina.com
*@Date:  2017/12/12
*@Param
*@Description:
*/
public enum DateFormat {
    date10("yyyy-MM-dd"), date8("yyyyMMdd"), time8("HH:mm:ss"), time5("HH:mm"), time6("HHmmss"), time4("HHmm"), date10Stime8("yyyy-MM-dd HH:mm:ss"), date8time6("yyyyMMddHHmmss");

    private String format;

    DateFormat(String format) {
        this.format = format;
    }

    public String getValue() {
        return format;
    }

}
