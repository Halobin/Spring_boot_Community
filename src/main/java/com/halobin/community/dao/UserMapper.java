package com.halobin.community.dao;

import com.halobin.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findUserById(@Param("userId") int userId);
}
