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

    @Test
    void testMapper(){
//        List<DiscussPost> discussPostList = discussPostService.findDiscussPosts(0,0,10);
//        for(DiscussPost discussPost : discussPostList){
//            System.out.println(discussPost);
//        }

//        User user = userService.findUserByEmail("nowcoder149@sina.com");
////        System.out.println(user);
//        user.setId(555);
//        user.setUsername("halobininsert");
//        user.setEmail("halobininsert@163.com");
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        user.setSalt("1111");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://images.nowcoder.com/head/149t.png");
        user.setCreateTime(new Date());
        int i = userService.insertUser(user);
        System.out.println(user.getId());
//        System.out.println(discussPostService.getDiscussPostCount(0));
    }

    @Test
    void testMailSender(){
        mailClient.sendMail("fengbin_1025@163.com","test","this is a test!");
    }
}
