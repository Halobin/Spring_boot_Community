package com.halobin.community;

import com.halobin.community.dao.DiscussPostMapper;
import com.halobin.community.dao.LoginTicketMapper;
import com.halobin.community.entity.DiscussPost;
import com.halobin.community.entity.LoginTicket;
import com.halobin.community.entity.User;
import com.halobin.community.service.DiscussPostService;
import com.halobin.community.service.UserService;
import com.halobin.community.util.CommunityUtil;
import com.halobin.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    void testMapper(){


        LoginTicket loginTicket = loginTicketMapper.findLoginTicketByTicket("e9ac89c3712741e38b9d509df48a0b25");
        System.out.println(loginTicket);

        loginTicketMapper.updateStatusByTicket("e9ac89c3712741e38b9d509df48a0b25", 1);

        loginTicket = loginTicketMapper.findLoginTicketByTicket("e9ac89c3712741e38b9d509df48a0b25");
        System.out.println(loginTicket);

    }

    @Test
    void testMailSender(){
        mailClient.sendMail("fengbin_1025@163.com","test","this is a test!");
    }
}
