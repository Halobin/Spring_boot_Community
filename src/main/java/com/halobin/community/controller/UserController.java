package com.halobin.community.controller;

import com.halobin.community.annotation.LoginRequired;
import com.halobin.community.entity.User;
import com.halobin.community.service.FollowService;
import com.halobin.community.service.LikeService;
import com.halobin.community.service.UserService;
import com.halobin.community.util.CommunityConstant;
import com.halobin.community.util.CommunityUtil;
import com.halobin.community.util.CookieUtil;
import com.halobin.community.util.HostHolder;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeaderUrl(MultipartFile uploadImag, Model model){
        if(uploadImag == null){
            model.addAttribute("error","?????????????????????");
            return "/site/setting";
        }

        String filename = uploadImag.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","?????????????????????");
            return "/site/setting";
        }

        filename = CommunityUtil.generateUUID() + suffix;
        File dest = new File(uploadPath + "/" + filename);
        try {
            uploadImag.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeaderUrlById(user.getId(), headerUrl);
        return "redirect:/index";
    }

    @GetMapping("/header/{filename}")
    public void getHeaderImag(@PathVariable("filename") String filename, HttpServletResponse response){
        filename = uploadPath + "/" + filename;
        String suffix = filename.substring(filename.lastIndexOf("."));
        response.setContentType("image/" + suffix);
        try (
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(filename);
                ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while((b = fis.read(buffer)) != -1){
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @LoginRequired
    @PostMapping("/setpassword")
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword, Model model, HttpServletRequest request){
        if(StringUtils.isBlank(oldPassword)){
            model.addAttribute("oldPasswordMsg","????????????????????????");
            return "/site/setting";
        }
        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg","????????????????????????");
            return "/site/setting";
        }
        if(!newPassword.equals(confirmPassword)){
            model.addAttribute("confirmPasswordMsg","????????????????????????");
            return "/site/setting";
        }

        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if(!user.getPassword().equals(oldPassword)){
            model.addAttribute("oldPasswordMsg","????????????????????????");
            return "/site/setting";
        }

        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userService.updatePasswordById(user.getId(), newPassword);
        String ticket = CookieUtil.getValue(request, "ticket");
        userService.userLogout(ticket);
//        userService.updateLoginTicketStatusByTicket(ticket, 1);
        return "redirect:/index";
    }

    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("???????????????");
        }

        // ??????
        model.addAttribute("user",user);
        // ????????????
        long likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);
        // ????????????
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //????????????
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //????????????
        boolean hasFollowed = false;
        if(hostHolder.getUser() != null){
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }
}
