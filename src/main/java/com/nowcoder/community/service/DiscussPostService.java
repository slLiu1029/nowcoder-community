package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper mapper;

    // 查询帖子
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return mapper.selectDiscussPosts(userId, offset, limit);
    }

    // 查询帖子数量
    public int findDiscussPostRows(int userId) {
        return mapper.selectDiscussPostRows(userId);
    }
}
