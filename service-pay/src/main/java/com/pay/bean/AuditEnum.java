package com.pay.bean;

/**
 * Created by zwj on 2018/11/5.
 */
public enum AuditEnum {
    AUDITING("0"),//审核中
    PASS("1"),//审核通过
    FAILED("2"),//审核驳回
    SUCCESS("3"),//开户成功
    CONFIRMING("4");//待确认
    private String code;
    AuditEnum(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
