package com.nowcoder.community.util;

public class RedisKeyUtil {
    private static final String SPLIT = ":";    // 单词分隔符
    private static final String PREFIX_ENTITY_LIKE = "like:entity"; // 点赞key的前缀
    private static final String PREFIX_USER_LIKE = "like:user"; // user点赞的前缀

    // 某个实体的赞
    // redis中对应的key->value是  like:entity:{entityType}:{entityId} -> set(userId)
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞
    // redis中对应的key->value是 like:user:userId -> int（点赞数）
    public static String getUserLikeKey(int userId) {
        return PREFIX_ENTITY_LIKE + SPLIT + userId;
    }
}
