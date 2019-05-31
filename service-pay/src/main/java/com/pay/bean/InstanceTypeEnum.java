package com.pay.bean;

/**
 * Created by zwj on 2019/1/21.
 */
public enum  InstanceTypeEnum {
    DEPOSIT("0"),//押金
    CONSUME("1"),//支付
    CONVENIENT("2"),//便民缴费
    DATA("3"),//数据传输
    SIGNATURE("4"),//电子签
    AUTH("5");//实名认证
    private String code;
    InstanceTypeEnum(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
