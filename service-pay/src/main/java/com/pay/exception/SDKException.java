/**
 * SDK异常
 */
package com.pay.exception;

public class SDKException extends Exception {

    private static final long serialVersionUID = 7634266621864608219L;

    private String code;

    private String msg;

    private String traceId;

    public SDKException(SDKExType code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "{\"code\":\"" + code + "\",\"msg\":\"[" + code + "]" + msg + "\",\"traceId\":\"OPEN-00-LOCAL-"
                + code.substring(code.length() - 3, code.length()) + "\"}";
    }

}