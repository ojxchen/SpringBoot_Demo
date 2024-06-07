package com.ojxchen.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel("sendEmailCodeRequest")
public class sendEmailCodeRequest {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]{5,}$")
    @ApiModelProperty("用户名，不为空且规则为^[a-zA-Z][a-zA-Z0-9_-]{5,}$")
    private String username;  // 用户名

    @NotBlank
    @Email
    @ApiModelProperty("用户邮箱，不为空且为正确邮箱规则")
    private String email;  // 邮箱

}
