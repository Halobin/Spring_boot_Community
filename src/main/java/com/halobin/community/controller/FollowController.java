package com.halobin.community.controller;

import com.halobin.community.entity.Page;
import com.halobin.community.entity.User;
import com.halobin.community.service.FollowService;
import com.halobin.community.service.UserService;
import com.halobin.community.util.CommunityConstant;
import com.halobin.community.util.CommunityUtil;
import com.halobin.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant{

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0,"已关注！");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType, int entityId){
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0,"已取消关注！");
    }

    @GetMapping("/followees/{userId}")
    public String followees(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setCount((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> followees = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        for(Map<String, Object> followee : followees){
            User u = (User) followee.get("user");
            followee.put("hasFollowed", hasFollowed(u.getId()));
        }
        model.addAttribute("users",followees);
        return "/site/followee";
    }

    @GetMapping("/followers/{userId}")
    public String followers(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("用户不存在");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setCount((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> followers = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        for(Map<String, Object> follower : followers){
            User u = (User) follower.get("user");
            follower.put("hasFollowed", hasFollowed(u.getId()));
        }
        model.addAttribute("users",followers);
        return "/site/follower";
    }

    public boolean hasFollowed(int userId){
        if(hostHolder.getUser() == null)    return false;

        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
