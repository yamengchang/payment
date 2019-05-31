package com.pay.bean;

public enum  XYPayChannelEnum {
    APP("app"),
    CSB("csb"),
    PUB("pub"),
    REPORT("report"),
    AUTH("auth");
    private String code;
    XYPayChannelEnum(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
