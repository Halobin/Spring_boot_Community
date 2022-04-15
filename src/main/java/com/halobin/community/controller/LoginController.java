package com.halobin.community.controller;

import com.halobin.community.entity.User;
import com.halobin.community.service.UserService;
import com.halobin.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private  UserService userService;

    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }

    /**
     * 注册
     *
     * @param model
     * @param user
     * @return
     */
    @PostMapping("/register")
    public String registerUser(Model model, User user){
        Map<String, Object> map = userService.registerUser(user);
        if(map == null || map.isEmpty()) {
            model.addAttribute("msg", "恭喜您注册成功，我们已经向您发送一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }

    /**
     * 账号激活
     *
     * @param model
     * @param userId
     * @param code
     * @return
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int res = userService.activation(userId, code);
        if(res == ACTIVATION_SUCCESS){
            model.addAttribute("msg", "恭喜您激活成功，您的账号可以正常使用！");
            model.addAttribute("target", "/login");
            return "/site/operate-result";
        }else if(res == ACTIVATION_FAILURE){
            model.addAttribute("msg", "激活失败，您的激活码错误！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("msg", "无效操作，您的账号已经激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }
    }
}
