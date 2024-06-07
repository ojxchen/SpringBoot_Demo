package com.ojxchen.R;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("R")
public class R<T> {

    @ApiModelProperty("状态码，200为响应成功，500为响应失败，501为登录状态异常")
    private int code;  // 响应状态码
    @ApiModelProperty("响应消息")
    private String message;  // 响应消息
    @ApiModelProperty("响应数据")
    private T data;  // 响应数据

    public R() {}

    public R(int code, String message, T data) {
        this.code = code;  // 设置状态码
        this.message = message;  // 设置消息
        this.data = data;  // 设置数据
    }

    // 成功响应的静态方法
    public static <T> R<T> success(T data) {
        return new R<>(200, "Success", data);
    }

    // 失败响应的静态方法
    public static <T> R<T> failure(String message) {
        return new R<>(500, message, null);
    }

    //  未授权响应的静态方法
    public static <T> R<T> error(String message) {
        return new R<>(501, message, null);
    }

    // 找不到
    public static <T> R<T> nothing(String message) {
        return new R<>(404, message, null);
    }



}

