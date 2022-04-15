package com.halobin.community.controller;

import com.google.code.kaptcha.Producer;
import com.halobin.community.entity.User;
import com.halobin.community.service.UserService;
import com.halobin.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.soap.Text;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private  UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

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

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //验证码存入session
        session.setAttribute("kaptcha", text);

        response.setContentType("image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param code
     * @param rememberme
     * @param model
     * @param response
     * @param session
     * @return
     */
    @PostMapping("/login")
    public String userLogin(String username, String password, String code, boolean rememberme,
                            Model model, HttpServletResponse response, HttpSession session){
        String kaptcha = (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确！");
            return "/site/login";
        }

        long expiredSecond = (rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS);
        Map<String, Object> map = userService.userLogin(username, password, expiredSecond);
        if(map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("codeMsg", map.get("codeMsg"));
            return "/site/login";
        }
    }

    /**
     * 退出登录
     *
     * @param ticket
     * @return
     */
    @GetMapping("/logout")
    public String userLogout(@CookieValue("ticket") String ticket){
        userService.userLogout(ticket);
        return "redirect:/login";
    }

    @GetMapping("/img")
    public void getImg(HttpServletResponse response, HttpSession session){
        response.setContentType("image/png");
        try {
            BufferedImage image = ImageIO.read(Files.newInputStream(Paths.get("C:\\Users\\Halobin\\Desktop\\svm.jpg")));
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
