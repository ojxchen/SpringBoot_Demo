package com.ojxchen.common;


//自定义异常类
public class CustomException  extends RuntimeException{
    private int code;

    public CustomException(String message, int code) {
        super(message);
        this.code = code;
    }

    public CustomException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
