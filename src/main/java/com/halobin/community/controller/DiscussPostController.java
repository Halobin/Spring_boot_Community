package com.halobin.community.controller;

import com.halobin.community.dao.DiscussPostMapper;
import com.halobin.community.entity.Comment;
import com.halobin.community.entity.DiscussPost;
import com.halobin.community.entity.Page;
import com.halobin.community.entity.User;
import com.halobin.community.service.CommentService;
import com.halobin.community.service.DiscussPostService;
import com.halobin.community.service.UserService;
import com.halobin.community.util.CommunityConstant;
import com.halobin.community.util.CommunityUtil;
import com.halobin.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discusspost")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user == null){
            return CommunityUtil.getJSONString(403,"请登录后再发布帖子！");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        return CommunityUtil.getJSONString(0,"发布成功！");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPostDetail(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //获取帖子详情
        DiscussPost post = discussPostService.findDiscussPost(discussPostId);
        model.addAttribute("post",post);
        //获取帖子作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user",user);

        page.setPath("/discusspost/detail/" + post.getId());
        page.setCount(post.getCommentCount());
        page.setLimit(5);

        //获取帖子评论
        List<Comment> commentList = commentService.getCommentByEntity(
                post.getId(), ENTITY_TYPE_POST, page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for(Comment comment: commentList){
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                List<Comment> replyList = commentService.getCommentByEntity(
                        comment.getId(), ENTITY_TYPE_COMMENT, 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply : replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));

                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                int replyCount = commentService.getCommentCountByEntity(comment.getId(), ENTITY_TYPE_COMMENT);
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }



}
