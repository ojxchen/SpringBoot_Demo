package com.ojxchen.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel("UpdatePasswordRequest")
public class UpdatePasswordRequest {
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    @ApiModelProperty("旧密码，不为空且规则为^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    private String oldPassword;  // 旧密码

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    @ApiModelProperty("新密码，不为空且规则为^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    private String newPassword;  // 新密码

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    @ApiModelProperty("确认密码，不为空且规则为^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$")
    private String confirmPassword;
}
