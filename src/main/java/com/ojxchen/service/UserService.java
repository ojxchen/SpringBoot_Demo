package com.ojxchen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ojxchen.entity.User;

public interface UserService extends IService<User> {

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     * @throws Exception
     */
    User selectByUsername(String username) throws Exception;

    /**
     * 根据邮箱查找用户
     * @param email
     * @return
     * @throws Exception
     */
    User selectByEmail(String email) throws Exception;

    /**
     * 根据用户名修改Ip和时间
     * @param username
     * @param lastLoginIp
     * @throws Exception
     */
    void updateByName(String username,String lastLoginIp) throws Exception;

    /**
     * 根据用户名修改密码
     * @param username
     * @param newPassword
     * @throws Exception
     */
    void updateByNameAndNewPassword(String username,String newPassword) throws Exception;

    /**
     * 根据用户名修改邮箱
     * @param username
     * @param email
     * @throws Exception
     */
    void updateByNameAndEmail(String username,String email) throws Exception;

    /**
     * 根据用户名和邮箱查找用户
     * @param username
     * @param email
     * @return
     * @throws Exception
     */
    User selectByUserNameAndEmail(String username,String email) throws Exception;



}
