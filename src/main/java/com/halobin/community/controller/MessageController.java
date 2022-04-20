package com.halobin.community.controller;

import com.halobin.community.annotation.LoginRequired;
import com.halobin.community.entity.Message;
import com.halobin.community.entity.Page;
import com.halobin.community.entity.User;
import com.halobin.community.service.MessageService;
import com.halobin.community.service.UserService;
import com.halobin.community.util.CommunityUtil;
import com.halobin.community.util.HostHolder;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @LoginRequired
    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page){
        User user = hostHolder.getUser();

        //分页
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setCount(messageService.findConversationCount(user.getId()));

        //会话
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if(conversationList != null){
            for(Message conversation : conversationList){
                Map<String, Object> map = new HashMap<>();
                map.put("conversation", conversation);
                map.put("letterCount", messageService.findLetterCount(conversation.getConversationId()));
                map.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), conversation.getConversationId()));
                int targetId = user.getId() == conversation.getFromId() ? conversation.getToId() : conversation.getFromId();
                map.put("target", userService.findUserById(targetId));

                conversations.add(map);
            }
        }
        model.addAttribute("conversations",conversations);
        model.addAttribute("unreadletterCount", messageService.findLetterUnreadCount(user.getId(), null));
        return "/site/letter";
    }

    @LoginRequired
    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page){

        //分页
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setCount(messageService.findLetterCount(conversationId));

        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if(letterList != null){
            for(Message letter : letterList){
                Map<String, Object> map = new HashMap<>();
                map.put("letter",letter);
                map.put("fromUser",userService.findUserById(letter.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters",letters);
        model.addAttribute("target",getLettersTarget(conversationId));

        return "/site/letter-detail";
    }

    public User getLettersTarget(String conversationId){
        String[] s = conversationId.split("_");
        int id0 = Integer.parseInt(s[0]);
        int id1 = Integer.parseInt(s[1]);

        if(hostHolder.getUser().getId() == id0){
            return userService.findUserById(id1);
        }else{
            return userService.findUserById(id0);
        }
    }

    @LoginRequired
    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String targetName, String content){
        User target = userService.findUserByUsername(targetName);
        if(target == null){
            return CommunityUtil.getJSONString(1,"目标用户不存在！");
        }

        Message letter = new Message();
        letter.setFromId(hostHolder.getUser().getId());
        letter.setToId(target.getId());
        if(letter.getFromId() < letter.getToId()){
            letter.setConversationId(letter.getFromId() + "_" + letter.getToId());
        }else{
            letter.setConversationId(letter.getToId() + "_" + letter.getFromId());
        }
        letter.setContent(content);
        letter.setCreateTime(new Date());
        messageService.addMessage(letter);

        return CommunityUtil.getJSONString(0);
    }
}
