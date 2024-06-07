package com.ojxchen.controller;

import com.ojxchen.R.R;
import com.ojxchen.dto.*;
import com.ojxchen.dto.response.LoginResponse;
import com.ojxchen.dto.response.LoginStatisticsResponse;
import com.ojxchen.service.AuthService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @ApiOperation("用户注册")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 500, message = "注册失败，用户已存在或邮箱已存在"),
    })
    @PostMapping("/register")
    public R<?> register(@Valid @RequestBody RegisterRequest request) throws Exception {
        return authService.register(request);
    }


    @ApiOperation("用户登录, 需设置Header Authorization:Bearer ${token}  Roles:${roles}")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功,如果记住密码，则设置cookie，有效期为5分钟",response = LoginResponse.class),
            @ApiResponse(code = 500, message = "登陆失败，用户不存在或密码错误"),
    })
    @PostMapping("/login")
    public R<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
        return authService.login(request, httpRequest,response);
    }

    @ApiOperation("退出")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 500, message = "登出失败"),
            @ApiResponse(code = 401, message = "token过期"),
    })
    @PostMapping ("/logout")
    public R<?> logout(HttpServletRequest request,HttpServletResponse response) throws Exception {
        return authService.logout(request,response);
    }

    @ApiOperation("修改密码")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 500, message = "用户不存在或旧密码错误"),
            @ApiResponse(code = 401, message = "token过期"),
    })
    @PostMapping("/update-Password")
    public R<?> updatePassword(@Valid @RequestBody UpdatePasswordRequest request, HttpServletRequest httpRequest) throws Exception {
        return authService.updatePassword(request, httpRequest);
    }

    @ApiOperation("修改邮箱")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 500, message = "用户不存在或邮箱已存在"),
            @ApiResponse(code = 401, message = "token过期"),
    })
    @PostMapping("/update-Email")
    public R<?> updateEmail(@Valid @RequestBody UpdateEmailRequest request, HttpServletRequest httpRequest) throws Exception {
        return authService.updateEmail(request, httpRequest);
    }

    @ApiOperation("发送验证码")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 500, message = "用户或邮箱错误"),
    })
    @PostMapping("/sendEmailCode")
    public R<?> sendEmailCode(@Valid @RequestBody sendEmailCodeRequest request) throws Exception {
        return authService.sendEmailCode(request);
    }

    @ApiOperation("密码重置")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 500, message = "用户不存在或验证码失效或账号有误或验证码输入有误"),
    })
    @PostMapping("/ResetPassword")
    public R<?> ResetPassword(@Valid @RequestBody ResetPasswordRequest request) throws Exception {
        return authService.ResetPassword(request);
    }


    @ApiOperation("获取近7日每日登录数据")
    @ApiResponses({
            @ApiResponse(code = 200,message = "成功", response = LoginStatisticsResponse.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "用户有误"),
            @ApiResponse(code = 401, message = "token过期"),
    })
    @PostMapping("/getLoginStatistics")
    public R<?> getLoginStatistics(HttpServletRequest httpRequest) throws Exception {
        return authService.getLoginStatistics(httpRequest);
    }

    @ApiOperation("检测登录状态")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 501, message = "未提供有效的身份验证令牌"),
    })
    @PostMapping("/checkLoginStatus")
    public R<?> checkLoginStatus(HttpServletRequest httpRequest) throws Exception {
        return authService.checkLoginStatus(httpRequest);
    }

}
