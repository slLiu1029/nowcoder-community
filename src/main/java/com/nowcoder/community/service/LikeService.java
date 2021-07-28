package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 点赞。更新实体的赞信息和作者被赞总数
     * @param userId 当前用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @param entityUserId 实体的作者id
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {
        // 由于需要更新实体的赞和用户的赞数，因此需要引入redis事务机制
        redisTemplate.execute(new SessionCallback(){
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 获得实体和用户的likeKey
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);

                // 事务开启
                redisOperations.multi();

                if (isMember) {
                    // 点过赞，再点取消赞
                    redisTemplate.opsForSet().remove(entityLikeKey, userId);
                    // 作者赞+1
                    redisOperations.opsForValue().decrement(userLikeKey);
                } else {
                    // 没点赞，点赞后加入集合中
                    redisTemplate.opsForSet().add(entityLikeKey, userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }

                // 事务执行
                return redisOperations.exec();
            }
        });
    }

    /**
     * 查询某实体被点赞的数量
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 指定实体被点赞的数量
     */
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询指定userId的用户对指定实体的点赞状态
     * @param userId 用户id
     * @param entityType 实体类型
     * @param entityId 实体id
     * @return 点赞状态。0表示没点赞，1表示点赞了
     */
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    /**
     * 查询指定用户被赞总数
     * @param userId 用户id
     * @return 被赞总数
     */
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count.intValue();
    }
}
