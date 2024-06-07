package com.ojxchen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ojxchen.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
