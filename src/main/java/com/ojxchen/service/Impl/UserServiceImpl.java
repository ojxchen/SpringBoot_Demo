package com.ojxchen.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ojxchen.entity.User;
import com.ojxchen.mapper.UserMapper;
import com.ojxchen.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl  extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    /*
     * 根据用户名查找用户
     * */
    @Override
    public User selectByUsername(String username) {
        // 使用 MyBatis Plus 提供的 LambdaQueryWrapper 进行查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUsername, username);
        return userMapper.selectOne(queryWrapper);
    }

    /*
     * 根据邮箱查找用户
     * */
    @Override
    public User selectByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getEmail, email);
        return userMapper.selectOne(queryWrapper);
    }

    /*
     * 根据用户名修改Ip和时间
     * */
    @Override
    public void updateByName(String username,String lastLoginIp) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(User::getUsername,username);
        User updateUser = new User();
        updateUser.setLastLoginIp(lastLoginIp); //设置最后登录Ip
        updateUser.setLastLoginTime(Timestamp.valueOf(LocalDateTime.now()));  // 设置最后登录时间
        userMapper.update(updateUser, updateWrapper);
    }

    /*
     * 根据用户名修改密码
     * */
    @Override
    public void updateByNameAndNewPassword(String username, String newPassword) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(User::getUsername,username);
        User updateUser = new User();
        updateUser.setPassword(newPassword);
        userMapper.update(updateUser, updateWrapper);
    }

    /**
     * 根据用户名修改邮箱
     *
     * */
    @Override
    public void updateByNameAndEmail(String username, String email) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(User::getUsername,username);
        User updateUser = new User();
        updateUser.setEmail(email);
        userMapper.update(updateUser, updateWrapper);
    }

    /*
     * 根据用户名和邮箱查找用户
     * */
    @Override
    public User selectByUserNameAndEmail(String username, String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUsername, username).eq(User::getEmail,email);
        return userMapper.selectOne(queryWrapper);
    }


}
