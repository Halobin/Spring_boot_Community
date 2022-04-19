package com.halobin.community.controller;

import com.halobin.community.annotation.LoginRequired;
import com.halobin.community.dao.CommentMapper;
import com.halobin.community.entity.Comment;
import com.halobin.community.service.CommentService;
import com.halobin.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @PostMapping("/add/{postId}")
    @LoginRequired
    public String addComment(@PathVariable("postId") int postId, Comment comment){
        //修改评论作者、日期
        comment.setUserId(hostHolder.getUser().getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        commentService.addComment(comment);

        return "redirect:/discusspost/detail/" + postId;
    }
}
