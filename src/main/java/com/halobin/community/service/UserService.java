package com.halobin.community.service;

import com.halobin.community.dao.UserMapper;
import com.halobin.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findUserById(int userId){
        return userMapper.findUserById(userId);
    }
}
