package com.halobin.community.service;

import com.halobin.community.dao.UserMapper;
import com.halobin.community.entity.User;
import com.halobin.community.util.CommunityConstant;
import com.halobin.community.util.CommunityUtil;
import com.halobin.community.util.MailClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int userId){
        return userMapper.findUserById(userId);
    }

    public User findUserByUsername(String username){
        return userMapper.findUserByUsername(username);
    }

    public User findUserByEmail(String email){
        return userMapper.findUserByEmail(email);
    }

    public int insertUser(User user){
        return userMapper.insertUser(user);
    }

    public int updateStatusById(int userId, int status){
        return userMapper.updateStatusById(userId, status);
    }
    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    public Map<String, Object> registerUser(User user){
        Map<String, Object> map = new HashMap<>();
        User u = userMapper.findUserByUsername(user.getUsername());
        if(u != null){
            map.put("usernameMsg", "账号已经存在！");
            return map;
        }
        u = userMapper.findUserByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg", "邮箱已被注册！");
            return map;
        }

        //注册用户
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    /**
     * 账号激活
     *
     * @param userId
     * @param code
     * @return
     */
    public int activation(int userId, String code){
        User user = userMapper.findUserById(userId);
        if(user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        }else if(user.getStatus() == 0 && user.getActivationCode().equals(code)){
            userMapper.updateStatusById(userId, 1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }
}
