package com.ojxchen.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel("RegisterRequest")
public class RegisterRequest {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]{5,}$")
    @ApiModelProperty("用户名，不为空且规则为^[a-zA-Z][a-zA-Z0-9_-]{5,}$")
    private String username;  // 用户名

    @NotBlank
    @Email
    @ApiModelProperty("用户邮箱，不为空且为正确邮箱规则")
    private String email;  // 邮箱

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    @ApiModelProperty("密码，不为空且规则为^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    private String password;  // 密码

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    @ApiModelProperty("确认密码，不为空且规则为^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    private String confirmPassword;

    @ApiModelProperty("邮箱验证码")
    private String code;


}
