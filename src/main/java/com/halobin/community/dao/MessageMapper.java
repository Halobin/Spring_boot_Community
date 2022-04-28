package com.halobin.community.dao;

import com.halobin.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 查找所有会话
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查找所有会话数量
    int selectConversationCount(int userId);

    // 查询某个会话私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询某个会话私信数量
    int selectLetterCount(String conversationId);

    // 查询未读私信数量
    int selectLetterUnreadCount(int userId, String conversationId);

    // 增加私信
    int insertLetter(Message letter);

    // 修改私信状态
    int updateLetterStatus(List<Integer> ids, int status);

    // 查询某个主题最新通知
    Message selectLatestNotice(int userId, String topic);

    // 查询某个主题通知数
    int selectNoticeCount(int userId, String topic);

    // 查询某个主题未读通知数
    int selectUnreadNoticeCount(int userId, String topic);

    // 查询某个主题通知列表
    List<Message> selectNotices(int userId, String topic, int offset, int limit);
}
