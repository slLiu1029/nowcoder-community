package com.nowcoder.community.util;

/**
 * 常量接口，设置一些常量
 */
public interface CommunityConstant {
    // 激活成功
    int ACTIVATION_SUCCESS = 0;

    // 重复激活
    int ACTIVATION_REPEAT = 1;

    // 激活失败
    int ACTIVATION_FAILURE = 2;

    // 默认状态的登录凭证超时时间
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    // 勾上记住我的登录凭证超时时间：3个月
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;
}
