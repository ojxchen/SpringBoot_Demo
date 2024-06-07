package com.ojxchen.service.Impl;

import com.alibaba.druid.util.StringUtils;
import com.ojxchen.R.R;
import com.ojxchen.dto.*;
import com.ojxchen.dto.response.LoginResponse;
import com.ojxchen.dto.response.LoginStatisticsResponse;
import com.ojxchen.entity.LoginStatistics;
import com.ojxchen.entity.User;
import com.ojxchen.mapper.LoginStatisticsMapper;
import com.ojxchen.service.AuthService;
import com.ojxchen.service.UserService;
import com.ojxchen.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private LoginStatisticsMapper loginStatisticsMapper;

    @Autowired
    private JwtUtil jwtUtil;



    private JedisPool jedisPool = new JedisPool();

    Jedis jedis = jedisPool.getResource();


    /**
     * 用户注册
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public R<?> register(RegisterRequest request) throws Exception {

        if (userService.selectByUsername(request.getUsername()) != null) {
            return R.failure("用户已存在！");
        }
        if (userService.selectByEmail(request.getEmail()) != null) {
            return R.failure("邮箱已存在");
        }

        if(!request.getPassword().equals(request.getConfirmPassword())){
            return R.failure("设置密码与确认密码不同，请重新输入");
        }

        String code = "";
        try (Jedis jedis = jedisPool.getResource()) {
            code = jedis.get(request.getUsername() + "_code");
        }

        if (StringUtils.isEmpty(code)) {
            return R.failure("验证码失效或账号有误");
        }

        if (!code.equals(request.getCode())) {
            return R.failure("验证码输入有误");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(MD5Utils.encrypt(request.getPassword()));  //MD5加密
        user.setEmail(request.getEmail());
        user.setRoles("Admin");
        userService.save(user);
        jedis.del(request.getUsername() + "_code");
        return R.success("注册成功");
    }

    /**
     * 用户登录
     * @param request
     * @param httpRequest
     * @param httpResponse
     * @return
     * @throws Exception
     */
    @Transactional
    @Override
    public R<?> login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {

        User user = userService.selectByUsername(request.getUsername());

        if (user == null) {
            return R.failure("用户不存在，请输入正确用户名");
        }
        if (!user.getPassword().equals(MD5Utils.encrypt(request.getPassword()))) {
            return R.failure("密码错误");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername());
        jwtUtil.setTokenExpiryTime(30 * 60 * 1000);
        jedis.set(request.getUsername(), token); // 存储活动会话
        jedis.pexpire(request.getUsername(), 30 * 60 * 1000);

        if (request.isRememberMe()) {
            Cookie rememberMePwdCookie = new Cookie("rememberMePwd", request.getPassword());
            rememberMePwdCookie.setMaxAge(5 * 60); // 5分钟过期
            rememberMePwdCookie.setPath("/"); // Cookie的有效路径
            httpResponse.addCookie(rememberMePwdCookie);
        }

        // 设置响应数据
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setRoles(user.getRoles());
        if(user.getLastLoginTime() != null){
            LocalDateTime lastLoginTime  =  user.getLastLoginTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = lastLoginTime.format(formatter);
            loginResponse.setLastLoginTime(formattedDateTime);
        }else{
            loginResponse.setLastLoginTime("");
        }
        loginResponse.setLastLoginIp(user.getLastLoginIp());  //设置最后登录ip
        String realIpAddress = IpAddressUtil.getRealIpAddress(httpRequest);
        userService.updateByName(user.getUsername(), realIpAddress);  // 更新用户信息


        //插入登录记录
        LoginStatistics loginStatistics = new LoginStatistics();
        loginStatistics.setUserId(user.getId());
        loginStatistics.setCreateAt(Timestamp.valueOf(LocalDateTime.now()));
        loginStatisticsMapper.insert(loginStatistics);


        return R.success(loginResponse);  // 登录成功响应
    }


    /**
     * 用户登出
     * @param httpRequest
     * @param httpResponse
     * @return
     */
    @Override
    public R<?> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        // 获取请求中的 JWT Token
        String token = httpRequest.getHeader("Authorization");
        if (StringUtils.isEmpty(token) || !token.startsWith("Bearer ")) {
            return R.failure("未提供有效的身份验证令牌");
        }
        // 从 Token 中提取用户名
        String username = jwtUtil.getUsernameFromRequest(httpRequest);
        if(!token.replace("Bearer ","").equals(jedis.get(username))){
            return R.failure("未提供有效的身份验证令牌");
        }

        //清除token
        jedis.del(username);
        return R.success("登出成功");
    }


    /**
     * 修改密码
     * @param request
     * @param httpRequest
     * @return
     * @throws Exception
     */
    @Override
    public R<?> updatePassword(UpdatePasswordRequest request, HttpServletRequest httpRequest) throws Exception {
        // 获取请求中的 JWT Token
        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return R.failure("未提供有效的身份验证令牌");
        }
        // 从 Token 中提取用户名
        String username = jwtUtil.getUsernameFromRequest(httpRequest);
        if(!token.replace("Bearer ","").equals(jedis.get(username))){
            return R.failure("未提供有效的身份验证令牌");
        }

        User user = userService.selectByUsername(username);
        if (user == null) {
            return R.failure("用户不存在");
        }

        if (!user.getPassword().equals(MD5Utils.encrypt(request.getOldPassword()))) {
            return R.failure("旧密码错误");
        }

        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            return R.failure("新密码与确认密码不同，请重新输入");
        }

        if(MD5Utils.encrypt(request.getNewPassword()).equals(user.getPassword())){
            return R.failure("新密码与旧密码相同，请重新修改");
        }

        userService.updateByNameAndNewPassword(username, MD5Utils.encrypt(request.getNewPassword()));
        return R.success("修改密码成功");
    }

    /**
     * 修改邮箱
     * @param request
     * @param httpRequest
     * @return
     * @throws Exception
     */
    @Override
    public R<?> updateEmail(UpdateEmailRequest request, HttpServletRequest httpRequest) throws Exception {
        // 获取请求中的 JWT Token
        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return R.failure("未提供有效的身份验证令牌");
        }
        // 从 Token 中提取用户名
        String username = jwtUtil.getUsernameFromRequest(httpRequest);
        if(!token.replace("Bearer ","").equals(jedis.get(username))){
            return R.failure("未提供有效的身份验证令牌");
        }
        User user = userService.selectByUsername(username);
        if (user == null) {
            return R.failure("用户不存在");
        }

        User user1 = userService.selectByEmail(request.getNewEmail());
        if (user1 != null) {
            return R.failure("邮箱已存在");
        }

        String code = "";
        try (Jedis jedis = jedisPool.getResource()) {
            code = jedis.get(username + "_code");
        }

        if (StringUtils.isEmpty(code)) {
            return R.failure("验证码失效或账号有误");
        }

        if (!code.equals(request.getCode())) {
            return R.failure("验证码输入有误");
        }

        userService.updateByNameAndEmail(username, request.getNewEmail());
        jedis.del(username + "_code");
        return R.success("邮箱更新成功");
    }

    /**
     * 发送验证码
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public R<?> sendEmailCode(sendEmailCodeRequest request) throws Exception {
        String code = VerificationCodeUtil.generateVerificationCode();

        EmailUtil emailUtil = new EmailUtil();
        emailUtil.sendEmail(request.getEmail(), code);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(request.getUsername() + "_code", code);
            jedis.pexpire(request.getUsername() + "_code", 5 * 60 * 1000);
        }

        return R.success("验证码发送成功");
    }


    /**
     * 重置密码
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public R<?> ResetPassword(ResetPasswordRequest request) throws Exception {
        String code = "";
        User user = userService.selectByUserNameAndEmail(request.getUsername(), request.getEmail());
        if (user == null) {
            return R.failure("用户名或邮箱错误");
        }

        if(!request.getNewPassword().equals(request.getConfirmPassword())){
            return R.failure("新密码与确认密码不同，请重新输入");
        }

        try (Jedis jedis = jedisPool.getResource()) {
            code = jedis.get(request.getUsername() + "_code");
        }

        if (StringUtils.isEmpty(code)) {
            return R.failure("验证码失效或账号有误");
        }

        if (!code.equals(request.getCode())) {
            return R.failure("验证码输入有误");
        }

        userService.updateByNameAndNewPassword(request.getUsername(), MD5Utils.encrypt(request.getNewPassword()));
        jedis.del(request.getUsername() + "_code");
        return R.success("密码重置成功");
    }


    /**
     * 获取近七天记录数
     * @param httpRequest
     * @return
     * @throws Exception
     */
    @Override
    public R<?> getLoginStatistics(HttpServletRequest httpRequest) throws Exception {

        // 获取请求中的 JWT Token
        String token = httpRequest.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return R.failure("未提供有效的身份验证令牌");
        }
        // 从 Token 中提取用户名
        String username = jwtUtil.getUsernameFromRequest(httpRequest);
        if(!token.replace("Bearer ","").equals(jedis.get(username))){
            return R.failure("未提供有效的身份验证令牌");
        }

        if (StringUtils.isEmpty(username)) {
            return R.failure("用户有误");
        }

        User user = userService.selectByUsername(username);
        List<LoginStatisticsResponse> statistics = loginStatisticsMapper.countLoginStatisticsByUserIdInLast7Days(user.getId());

        return R.success(statistics);
    }

    /**
     * 登录状态检测
     * @param httpRequest
     * @return
     * @throws Exception
     */
    @Override
    public R<?> checkLoginStatus(HttpServletRequest httpRequest) throws Exception {
        String token = httpRequest.getHeader("Authorization").replace("Bearer ","");
        // 从 Token 中提取用户名
        String username = jwtUtil.getUsernameFromRequest(httpRequest);
        if(StringUtils.isEmpty(token) || !token.equals(jedis.get(username))){
            return  R.error("未提供有效的身份验证令牌");
        }else{
            jedis.pexpire(username,30*60*1000);
        }
        return R.success("登录状态正常");
    }


}
