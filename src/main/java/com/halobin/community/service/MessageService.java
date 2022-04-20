package com.halobin.community.service;

import com.halobin.community.dao.MessageMapper;
import com.halobin.community.entity.Message;
import com.halobin.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    public int findConversationCount(int userId){
        return messageMapper.selectConversationCount(userId);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit){
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    public int findLetterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(int userId, String conversationId){
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    public int addMessage(Message letter){
        letter.setContent(HtmlUtils.htmlEscape(letter.getContent()));
        letter.setContent(sensitiveFilter.filterText(letter.getContent()));

        return messageMapper.insertLetter(letter);
    }

    public int readMessage(List<Integer> ids, int status){
        return messageMapper.updateLetterStatus(ids, status);
    }
}
