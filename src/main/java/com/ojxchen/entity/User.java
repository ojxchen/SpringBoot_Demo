package com.ojxchen.entity;


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
@TableName("user")
@ApiModel("用户类")
public class User {
    @TableId
    private String id;  // 用户ID
    @ApiModelProperty("用户姓名")
    private String username;  // 用户名
    @ApiModelProperty("用户年龄")
    private String email;  // 邮箱
    @ApiModelProperty("用户密码")
    private String password;  // 密码
    @ApiModelProperty("用户最后登录IP")
    private String lastLoginIp;  // 最后登录IP
    @ApiModelProperty("用户最后登录时间")
    private Timestamp lastLoginTime;  // 最后登录时间
    @ApiModelProperty("权限")
    private String roles; //权限
}

