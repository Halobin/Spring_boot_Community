package com.halobin.community.dao;

import com.halobin.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findUserById(@Param("userId") int userId);

    User findUserByUsername(@Param("username") String username);

    User findUserByEmail(@Param("email") String email);

    int insertUser(User user);

    int updateStatusById(int userId, int status);

    int updateHeaderUrlById(int userId, String headerUrl);

    int updatePasswordById(int userId, String password);
}
