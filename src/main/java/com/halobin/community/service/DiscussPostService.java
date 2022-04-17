package com.halobin.community.service;

import com.halobin.community.dao.DiscussPostMapper;
import com.halobin.community.entity.DiscussPost;
import com.halobin.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int getDiscussPostCount(int userId){
        return discussPostMapper.selectDiscussPostCount(userId);
    }

    //增加帖子
    public int addDiscussPost(DiscussPost post){
        if(post == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        //转义html标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filterText(post.getTitle()));
        post.setContent(sensitiveFilter.filterText(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }
}
