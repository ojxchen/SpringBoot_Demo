package com.ojxchen.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("login_statistics")
@ApiModel("用户登录记录")
public class LoginStatistics {
    @TableId
    private String id;
    @ApiModelProperty("用户Id")
    private String userId;
    @TableField("create_at")
    @ApiModelProperty("用户登录时间")
    private Timestamp createAt;
}
