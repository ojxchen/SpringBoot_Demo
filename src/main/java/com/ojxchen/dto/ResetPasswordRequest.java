package com.ojxchen.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel("ResetPasswordRequest")
public class ResetPasswordRequest {

    @NotBlank
    @ApiModelProperty("用户名，不为空")
    private String username;

    @NotBlank
    @Email
    @ApiModelProperty("邮箱")
    private String email;

    @NotBlank
    @ApiModelProperty("验证码，不为空")
    private String code;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    @ApiModelProperty("新密码，且规则为^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    private String newPassword;  // 新密码

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    @ApiModelProperty("确认密码，不为空且规则为^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    private String confirmPassword;
}
