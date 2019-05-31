/**
 * SDK异常枚举类
 */
package com.pay.exception;

public enum SDKExType {

	CONN_ERR("OPEN25801", "通讯错误或超时，交易未决。"),

    PARAM_ERR("OPEN25802", "参数校验错误。"),

    UNKNOWN("OPEN25803", "未知错误，请检查是否为最新版本SDK或是否配置错误。");

    /**
     * 根据编码找枚举
     * 
     * @param code
     *            编码
     * @return SDKExType
     */
    public static SDKExType getByCode(String code) {
        if (code == null) {
            return null;
        }

        for (SDKExType t : values()) {
            if (t.getCode().equals(code)) {
                return t;
            }
        }
        return null;
    }

    private final String code;

    private final String msg;

    private SDKExType(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public SDKExType returnEnum(String persistedValue) {
        return getByCode(persistedValue);
    }

}