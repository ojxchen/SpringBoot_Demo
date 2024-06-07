package com.ojxchen.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("七天登录次数记录")
public class LoginStatisticsResponse {

    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("次数")
    private int count;
}
