package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    // 如果userId!=0，则根据用户id查询帖子并实现分页查询
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    // 查询帖子的行数，如果userId不是0，则返回指定用户的帖子有多少
    int selectDiscussPostRows(@Param("userId") int userId);

}
