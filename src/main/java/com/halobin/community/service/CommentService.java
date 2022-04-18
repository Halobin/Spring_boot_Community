package com.halobin.community.service;

import com.halobin.community.dao.CommentMapper;
import com.halobin.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public List<Comment> getCommentByEntity(int entityId, int entityType, int offset, int limit){
        return commentMapper.selectCommentByEntity(entityId, entityType, offset, limit);
    }

    public int getCommentCountByEntity(int entityId, int entityType){
        return commentMapper.selectCommentCountByEntity(entityId, entityType);
    }
}
