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
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;

    /**
     * 实体类型：用户
     */
    int ENTITY_TYPE_USER = 3;
}
