package com.halobin.community.controller;

import com.halobin.community.dao.DiscussPostMapper;
import com.halobin.community.entity.DiscussPost;
import com.halobin.community.entity.Page;
import com.halobin.community.entity.User;
import com.halobin.community.service.DiscussPostService;
import com.halobin.community.service.LikeService;
import com.halobin.community.service.UserService;
import com.halobin.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainPageController implements CommunityConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @RequestMapping("/index")
    public String getIndexPage(Model model, Page page){
        page.setCount(discussPostService.getDiscussPostCount(0));
        page.setPath("/index");

        List<DiscussPost> discussPostList = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String, Object>> discussPostsMap = new ArrayList<>();
        for(DiscussPost discussPost : discussPostList){
            Map<String, Object> map = new HashMap<>();
            map.put("post", discussPost);
            User user = userService.findUserById(discussPost.getUserId());
            map.put("user", user);
            discussPostsMap.add(map);
            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPost.getId());
            map.put("likeCount", likeCount);
        }
        model.addAttribute("discussPostsMap", discussPostsMap);
        return "/index";
    }

    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }
}
