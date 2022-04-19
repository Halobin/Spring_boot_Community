package com.halobin.community.dao;

import com.halobin.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentByEntity(int entityId, int entityType, int offset, int limit);

    int selectCommentCountByEntity(int entityId, int entityType);

    int insertComment(Comment comment);
}
