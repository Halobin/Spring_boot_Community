package com.halobin.community;

import com.halobin.community.dao.DiscussPostMapper;
import com.halobin.community.entity.DiscussPost;
import com.halobin.community.entity.User;
import com.halobin.community.service.DiscussPostService;
import com.halobin.community.service.UserService;
import com.halobin.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailClient mailClient;

    @Test
    void testMapper(){
        List<DiscussPost> discussPostList = discussPostService.findDiscussPosts(0,0,10);
        for(DiscussPost discussPost : discussPostList){
            System.out.println(discussPost);
        }

        User user = userService.findUserById(101);
        System.out.println(user);

        System.out.println(discussPostService.getDiscussPostCount(0));
    }

    @Test
    void testMailSender(){
        mailClient.sendMail("fengbin_1025@163.com","test","this is a test!");
    }
}
