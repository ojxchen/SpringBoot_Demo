package com.ojxchen.service;

import com.ojxchen.R.R;
import com.ojxchen.dto.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {

     /**
      * 用户注册
      * @param request
      * @return
      * @throws Exception
      */
     R<?> register(RegisterRequest request) throws Exception;

     /**
      * 用户登录
      * @param request
      * @param httpRequest
      * @param httpServletResponse
      * @return
      * @throws Exception
      */
     R<?> login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpServletResponse) throws Exception;

     /**
      * 用户退出
      * @param request
      * @param response
      * @return
      * @throws Exception
      */
     R<?> logout(HttpServletRequest request, HttpServletResponse response) throws Exception;

     /**
      * 修改密码
      * @param request
      * @param httpRequest
      * @return
      * @throws Exception
      */
     R<?> updatePassword(UpdatePasswordRequest request, HttpServletRequest httpRequest) throws Exception;

     /**
      * 修改邮箱
      * @param request
      * @param httpRequest
      * @return
      * @throws Exception
      */
     R<?> updateEmail(UpdateEmailRequest request, HttpServletRequest httpRequest) throws Exception;

     /**
      * 发送验证码
      * @param request
      * @return
      * @throws Exception
      */
     R<?> sendEmailCode(sendEmailCodeRequest request) throws Exception;

     /**
      * 重置密码
      * @param request
      * @return
      * @throws Exception
      */
     R<?> ResetPassword(ResetPasswordRequest request) throws Exception;

     /**
      * 获取近七天登录次数
      * @param httpRequest
      * @return
      * @throws Exception
      */
     R<?> getLoginStatistics(HttpServletRequest httpRequest) throws Exception;

     /**
      * 登录状态检测
      * @param httpRequest
      * @return
      * @throws Exception
      */
     R<?> checkLoginStatus(HttpServletRequest httpRequest) throws Exception;
}
