package com.ojxchen.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("LoginRequest")
public class LoginRequest {

    @NotBlank
    @ApiModelProperty("用户名，不为空")
    private String username;  // 用户名

    @NotBlank
    @ApiModelProperty("密码，不为空")
    private String password;  // 密码

    @ApiModelProperty("是否记住密码，true Or false")
    private boolean rememberMe;  // 记住密码

}
