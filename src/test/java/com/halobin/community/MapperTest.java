package com.halobin.community;

import com.halobin.community.dao.DiscussPostMapper;
import com.halobin.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class MapperTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testDiscussPostMapper(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle("test");
        discussPost.setContent("testcontent");
        discussPost.setUserId(667);
        discussPost.setCreateTime(new Date());

        discussPostMapper.insertDiscussPost(discussPost);
    }
}
