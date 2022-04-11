package com.halobin.community.controller;

import com.halobin.community.dao.DiscussPostMapper;
import com.halobin.community.entity.DiscussPost;
import com.halobin.community.entity.User;
import com.halobin.community.service.DiscussPostService;
import com.halobin.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainPageController {

    @Autowired
    private UserService userService;

    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping("/index")
    public String getIndexPage(Model model){
        List<DiscussPost> discussPostList = discussPostService.findDiscussPosts(0,0,10);
        List<Map<String, Object>> discussPostsMap = new ArrayList<>();
        for(DiscussPost discussPost : discussPostList){
            Map<String, Object> map = new HashMap<>();
            map.put("post", discussPost);
            User user = userService.findUserById(discussPost.getUserId());
            map.put("user", user);
            discussPostsMap.add(map);
        }
        model.addAttribute("discussPostsMap", discussPostsMap);
        return "/index";
    }
}
