package com.halobin.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.halobin.community.annotation.LoginRequired;
import com.halobin.community.entity.Message;
import com.halobin.community.entity.Page;
import com.halobin.community.entity.User;
import com.halobin.community.service.MessageService;
import com.halobin.community.service.UserService;
import com.halobin.community.util.CommunityConstant;
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
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {

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
        model.addAttribute("unreadnoticeCount", messageService.findUnreadNoticeCount(user.getId(), null));
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

        List<Integer> unreadLetterIds = getUnreadLetterIds(letterList);
        if(!unreadLetterIds.isEmpty()){
            messageService.readMessage(unreadLetterIds, 1);
        }

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

    public List<Integer> getUnreadLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();

        if(letterList != null){
            for(Message letter : letterList){
                if(letter.getToId() == hostHolder.getUser().getId() && letter.getStatus() == 0){
                    ids.add(letter.getId());
                }
            }
        }

        return ids;
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

    @LoginRequired
    @GetMapping("/notice/list")
    public String getNoticeList(Model model){
        User user = hostHolder.getUser();

        // 查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        if(message != null){
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("count", count);

            int unreadCount = messageService.findUnreadNoticeCount(user.getId(), TOPIC_COMMENT);
            messageVo.put("unreadCount", unreadCount);

            model.addAttribute("commentNotice", messageVo);
        }
        // 查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        if(message != null){
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));
            messageVo.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("count", count);

            int unreadCount = messageService.findUnreadNoticeCount(user.getId(), TOPIC_LIKE);
            messageVo.put("unreadCount", unreadCount);

            model.addAttribute("likeNotice", messageVo);
        }
        // 查询关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        if(message != null){
            Map<String, Object> messageVo = new HashMap<>();
            messageVo.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVo.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVo.put("entityType", data.get("entityType"));
            messageVo.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count", count);

            int unreadCount = messageService.findUnreadNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageVo.put("unreadCount", unreadCount);

            model.addAttribute("followNotice", messageVo);
        }
        //查询未读信息数量
        int unreadLetterCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("unreadletterCount", unreadLetterCount);
        int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), null);
        model.addAttribute("unreadnoticeCount", unreadNoticeCount);
        return "/site/notice";
    }

    @LoginRequired
    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Model model, Page page){
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setCount(messageService.findNoticeCount(user.getId(), topic));
        List<Message> list = messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVoList = new ArrayList<>();
        if(list != null){
            for(Message notice : list){
                Map<String, Object> map = new HashMap<>();
                map.put("notice", notice);
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));

                map.put("fromUser",userService.findUserById(notice.getFromId()));
                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);
        //设置已读
        List<Integer> ids = getUnreadLetterIds(list);
        if(!ids.isEmpty()){
            messageService.readMessage(ids, 1);
        }
        return "/site/notice-detail";
    }
}
