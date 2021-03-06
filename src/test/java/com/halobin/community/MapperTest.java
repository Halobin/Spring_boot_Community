package com.halobin.community;

import com.halobin.community.dao.CommentMapper;
import com.halobin.community.dao.DiscussPostMapper;
import com.halobin.community.dao.MessageMapper;
import com.halobin.community.entity.Comment;
import com.halobin.community.entity.DiscussPost;
import com.halobin.community.entity.Message;
import com.halobin.community.util.CommunityConstant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class MapperTest implements CommunityConstant {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testDiscussPostMapper(){
        DiscussPost discussPost = new DiscussPost();
        discussPost.setTitle("test");
        discussPost.setContent("testcontent");
        discussPost.setUserId(667);
        discussPost.setCreateTime(new Date());

        discussPostMapper.insertDiscussPost(discussPost);
    }

    @Test
    public void testCommentMapper(){
        List<Comment> commentList = commentMapper.selectCommentByEntity(228, ENTITY_TYPE_POST, 0, Integer.MAX_VALUE);
        for(Comment comment:commentList){
            System.out.println(comment);
        }
    }

    @Test
    public void testMessageMapper(){
        List<Message> messageList = messageMapper.selectConversations(111, 0, Integer.MAX_VALUE);
        for(Message mes : messageList){
            System.out.println(mes);
        }
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        messageList = messageMapper.selectLetters("111_112",0, Integer.MAX_VALUE);
        for(Message mes : messageList){
            System.out.println(mes);
        }
        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        System.out.println(messageMapper.selectLetterUnreadCount(131,"111_131"));
    }
}
