package com.ojxchen;

import com.ojxchen.R.R;
import com.ojxchen.dto.*;
import com.ojxchen.dto.response.LoginResponse;
import com.ojxchen.entity.User;
import com.ojxchen.service.AuthService;
import com.ojxchen.service.UserService;
import com.ojxchen.util.EmailUtil;
import com.ojxchen.util.MD5Utils;
import com.ojxchen.util.VerificationCodeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class AuthServiceImplTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;


    /*
    * 注册测试
    * */
    @Test
    public void testRegister_Success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("object");
        registerRequest.setPassword("Xc123456");
        registerRequest.setEmail("3299765893@qq.com");
        R<?> result = authService.register(registerRequest);
        System.out.println(result.getMessage());

        assertEquals("Success", result.getMessage());
    }

    /*
    * 登录测试
    * */
    @Test
    public void testLogin_Success() throws Exception {

        // 创建登录请求
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("object");
        loginRequest.setPassword("Xc123456");

        // 执行登录操作
        R<?> result = authService.login(loginRequest, mock(HttpServletRequest.class), mock(HttpServletResponse.class));

        // 验证登录成功
        assertEquals("Success", result.getMessage());
        System.out.println(result.getData());
        assertNotNull(((LoginResponse) result.getData()).getToken());
        assertNotNull(((LoginResponse) result.getData()).getRoles());
        assertNotNull(((LoginResponse) result.getData()).getLastLoginTime());
    }

    /*
    * 修改密码测试
    * */
    @Test
    public void testUpdatePassword_Success() throws Exception {
        // 准备测试数据
        String username = "object";
        String oldPassword = "Xc123456";
        String newPassword = "Xc111111";
        String encryptedNewPassword = MD5Utils.encrypt(newPassword);


        // 模拟HttpServletRequest对象
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvYmplY3QiLCJleHAiOjE3MTczODY1MDh9.Va3AsHuJAEH5buzzH6F_wVS1HGLkljVXrVCnR43Zcu4TVQ_EFt6PTkLpXsdeALj43HiJyzyjY_XIkDZ1y0VfHA");
        when(request.getHeader("Roles")).thenReturn("Admin");


        // 创建UpdatePasswordRequest对象
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setOldPassword(oldPassword);
        updatePasswordRequest.setNewPassword(newPassword);

        // 执行更新密码操作
        R<?> result = authService.updatePassword(updatePasswordRequest, request);

        // 验证结果是否符合预期
        assertEquals("Success", result.getMessage());

        // 验证用户密码是否更新成功
        Optional<User> updatedUserOptional = Optional.ofNullable(userService.selectByUsername(username));
        if (updatedUserOptional.isPresent()) {
            User updatedUser = updatedUserOptional.get();
            assertEquals(encryptedNewPassword, updatedUser.getPassword());
        } else {
            // 如果找不到用户，测试失败
            assertEquals(true, false);
        }
    }


    /*
    * 修改邮箱测试
    * */
    @Test
    public void testUpdateEmail_Success() throws Exception {
        // 准备测试数据
        String username = "object";
        String newEmail = "123456789@qq.com";

        // 模拟HttpServletRequest对象
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvYmplY3QiLCJleHAiOjE3MTczODY2NzB9.Yl9MdLhUMivMwAE3gqQoGnDa59GCQougRFk45f03D-aa1RURbVoB8FJwz0HznT3swsmc_vEiFneu3nRfQZbPfQ");
        when(request.getHeader("Roles")).thenReturn("Admin");

        // 创建UpdateEmailRequest对象
        UpdateEmailRequest updateEmailRequest = new UpdateEmailRequest();
        updateEmailRequest.setNewEmail(newEmail);

        // 执行更新邮箱操作
        R<?> result = authService.updateEmail(updateEmailRequest, request);

        // 验证结果是否符合预期
        assertEquals("Success", result.getMessage());

        // 验证用户邮箱是否更新成功
        Optional<User> updatedUserOptional = Optional.ofNullable(userService.selectByUsername(username));
        if (updatedUserOptional.isPresent()) {
            User updatedUser = updatedUserOptional.get();
            assertEquals(newEmail, updatedUser.getEmail());
        } else {
            // 如果找不到用户，测试失败
            assertEquals(true, false);
        }
    }

    /*
    * 忘记密码测试
    * */
    @Test
    public void testForgotPassword_Success() throws Exception {
        // 准备测试数据
        String username = "ojxchen";
        String email = "1758754717@qq.com";


        // 创建ForgotPasswordRequest对象
        sendEmailCodeRequest forgotPasswordRequest = new sendEmailCodeRequest();
        forgotPasswordRequest.setUsername(username);
        forgotPasswordRequest.setEmail(email);

        // 模拟EmailUtil发送邮件
        EmailUtil emailUtil = mock(EmailUtil.class);

        // 模拟VerificationCodeUtil生成验证码
        String code = "123456";
        mockStatic(VerificationCodeUtil.class);
        when(VerificationCodeUtil.generateVerificationCode()).thenReturn(code);

        // 执行忘记密码操作
        R<?> result = authService.sendEmailCode(forgotPasswordRequest);

        // 验证结果是否符合预期
        assertEquals("Success", result.getMessage());

    }

    /*
    * 重置密码测试
    * */
    @Test
    public void testResetPassword_Success() throws Exception {
        // 准备测试数据
        String username = "ojxchen";
        String code = "123456";
        String newPassword = "Xc123456";

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setUsername(username);
        request.setCode(code);
        request.setNewPassword(newPassword);


        // 执行被测试的方法
        R<?> result = authService.ResetPassword(request);

        // 验证方法调用和返回结果
        assertEquals("Success", result.getMessage());
    }


    /*
    * 记录登录记录次数测试
    * */
    @Test
    public void testGetLoginStatistics_Success() throws Exception {
        String token = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvYmplY3QiLCJleHAiOjE3MTczODY2NzB9.Yl9MdLhUMivMwAE3gqQoGnDa59GCQougRFk45f03D-aa1RURbVoB8FJwz0HznT3swsmc_vEiFneu3nRfQZbPfQ";

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn(token);

        R<?> result = authService.getLoginStatistics(request);
        assertEquals("Success", result.getMessage());

    }

    /*
    * 状态检测测试
    * */
    @Test
    public void checkLoginStatus() throws Exception {
        // 模拟HttpServletRequest对象
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvYmplY3QiLCJleHAiOjE3MTczODY2NzB9.Yl9MdLhUMivMwAE3gqQoGnDa59GCQougRFk45f03D-aa1RURbVoB8FJwz0HznT3swsmc_vEiFneu3nRfQZbPfQ");
        when(request.getHeader("Roles")).thenReturn("Admin");

        R<?> result = authService.checkLoginStatus(request);
        assertEquals("Success",result.getMessage());

    }


    /*
     * 状态检测失效测试
     * */
    @Test
    public void checkFailureLoginStatus() throws Exception {
        // 模拟HttpServletRequest对象
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvYmplY3QiLCJleHAiOjE3MTczODY2NzB9.Yl9MdLhUMivMwAE3gqQoGnDa59GCQougRFk45f03D-aa1RURbVoB8FJwz0HznT3swsmc_vEiFneu3nRfQZbPfQ");
        when(request.getHeader("Roles")).thenReturn("Admin");

        R<?> result = authService.checkLoginStatus(request);
        assertEquals(403,result.getCode());

    }


}
