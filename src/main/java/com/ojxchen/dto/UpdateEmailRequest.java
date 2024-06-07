package com.ojxchen.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel("UpdateEmailRequest")
public class UpdateEmailRequest {

    @NotBlank
    @Email
    @ApiModelProperty("新邮箱，且为正确邮箱规则")
    private String newEmail;  // 邮箱

    @ApiModelProperty("邮箱验证码")
    private String code;
}
