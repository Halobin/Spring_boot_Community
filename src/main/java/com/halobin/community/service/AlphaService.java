package com.halobin.community.service;

import com.halobin.community.dao.UserMapper;
import com.halobin.community.entity.User;
import com.halobin.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class AlphaService {

    @Autowired
    private UserMapper userMapper;


    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public Object save1(){
        User user = new User();

        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@163.com");
        user.setCreateTime(new Date());

        Integer.valueOf("ni");

        return "ok";
    }
}
