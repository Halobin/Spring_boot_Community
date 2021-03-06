package com.halobin.community.service;

import com.halobin.community.dao.LoginTicketMapper;
import com.halobin.community.dao.UserMapper;
import com.halobin.community.entity.LoginTicket;
import com.halobin.community.entity.User;
import com.halobin.community.util.CommunityConstant;
import com.halobin.community.util.CommunityUtil;
import com.halobin.community.util.MailClient;
import com.halobin.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private RedisTemplate redisTemplate;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    public User findUserById(int userId){
        User user = getUserCache(userId);
        if(user == null){
            user = initUserCache(userId);
        }
        return user;
    }

    public User findUserByUsername(String username){
        return userMapper.findUserByUsername(username);
    }

//    public User findUserByEmail(String email){
//        return userMapper.findUserByEmail(email);
//    }

//    public int insertUser(User user){
//        return userMapper.insertUser(user);
//    }

    public int updateStatusById(int userId, int status){
        int rows = userMapper.updateStatusById(userId, status);
        clearUserCache(userId);
        return rows;
    }

    /**
     * ????????????
     *
     * @param user
     * @return
     */
    public Map<String, Object> registerUser(User user){
        Map<String, Object> map = new HashMap<>();
        User u = userMapper.findUserByUsername(user.getUsername());
        if(u != null){
            map.put("usernameMsg", "?????????????????????");
            return map;
        }
        u = userMapper.findUserByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg", "?????????????????????");
            return map;
        }

        //????????????
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //??????????????????
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "????????????", content);

        return map;
    }

    /**
     * ????????????
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
            clearUserCache(userId);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * ????????????
     *
     * @param username
     * @param password
     * @param expiredSeconds
     * @return
     */
    public Map<String, Object> userLogin(String username, String password, long expiredSeconds){
        Map<String, Object> map = new HashMap<>();

        //????????????
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","?????????????????????");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","?????????????????????");
            return map;
        }

        //????????????
        User user = userMapper.findUserByUsername(username);
        if(user == null){
            map.put("usernameMsg","??????????????????");
            return map;
        }

        //????????????
        if(user.getStatus() == 0){
            map.put("usernameMsg","??????????????????");
            return map;
        }

        //????????????
        password = CommunityUtil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","???????????????");
            return map;
        }

        //??????????????????
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);
        String ticketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(ticketKey,loginTicket);

        map.put("ticket", loginTicket.getTicket());

        return map;
    }

    /**
     * ????????????
     *
     * @param ticket
     */
    public void userLogout(String ticket){
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(ticketKey,loginTicket);
//        loginTicketMapper.updateStatusByTicket(ticket, 1);
    }

    public LoginTicket findLoginTicketByTicket(String ticket){
        String ticketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        return loginTicket;
//        return loginTicketMapper.findLoginTicketByTicket(ticket);
    }

    public int updateHeaderUrlById(int userId, String headerUrl){
        int rows = userMapper.updateHeaderUrlById(userId, headerUrl);
        clearUserCache(userId);
        return rows;
    }

    public int updatePasswordById(int userId, String password){
        int rows = userMapper.updatePasswordById(userId, password);
        clearUserCache(userId);
        return rows;
    }

//    public int updateLoginTicketStatusByTicket(String ticket, int status){
//        return loginTicketMapper.updateStatusByTicket(ticket, 1);
//    }
    // ?????????????????????
    private User getUserCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }
    // ?????????????????????
    private User initUserCache(int userId){
        User user = userMapper.findUserById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey ,user,3600, TimeUnit.SECONDS);
        return user;
    }
    // ??????????????????
    private void clearUserCache(int userId){
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }
}
