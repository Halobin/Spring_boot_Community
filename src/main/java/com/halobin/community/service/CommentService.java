package com.halobin.community.service;

import com.halobin.community.dao.CommentMapper;
import com.halobin.community.entity.Comment;
import com.halobin.community.util.CommunityConstant;
import com.halobin.community.util.HostHolder;
import com.halobin.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 查询评论
     *
     * @param entityId
     * @param entityType
     * @param offset
     * @param limit
     * @return
     */
    public List<Comment> getCommentByEntity(int entityId, int entityType, int offset, int limit){
        return commentMapper.selectCommentByEntity(entityId, entityType, offset, limit);
    }

    /**
     * 查询评论数量
     *
     * @param entityId
     * @param entityType
     * @return
     */
    public int getCommentCountByEntity(int entityId, int entityType){
        return commentMapper.selectCommentCountByEntity(entityId, entityType);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment){
        if(comment == null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        //新增评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filterText(comment.getContent()));
        int row = commentMapper.insertComment(comment);

        //更新帖子评论数量
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            int count = commentMapper.selectCommentCountByEntity(comment.getEntityId(), comment.getEntityType());
            discussPostService.updateCommentCountById(comment.getEntityId(), count);
        }

        return row;
    }
}
