package com.halobin.community.util;

public interface CommunityConstant {

    /**
     *  激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 1;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 2;

    /**
     * 默认状态下登录凭证有效时间
     */
    long DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住状态下登录配置有效时间
     */
    long REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 5;

    /**
     * 评论实体：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 评论实体：评论
     */
    int ENTITY_TYPE_COMMENT = 2;
}
