package com.ojxchen.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@ApiModel("登录响应体")
@Data
public class LoginResponse {

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("最后登录ip")
    private String lastLoginIp;

    @ApiModelProperty("最后登录时间")
    private String lastLoginTime;

    private String roles;

}
