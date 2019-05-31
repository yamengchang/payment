package com.retailstore.ribbon.error;


/*
*@Author:Gavin
*@Email:gavinsjq@sina.com
*@Date:  2017/12/12
*@Param
*@Description:
*/
public class CustomException extends RuntimeException {

    private String code;
    private String msg;

    private String message;

    @Override
    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CustomException(String code) {
        this.code = code;
        this.msg = "";
    }

    public CustomException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CustomException(Integer code, String msg) {
        this.code = String.valueOf(code);
        this.msg = msg;
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
}
