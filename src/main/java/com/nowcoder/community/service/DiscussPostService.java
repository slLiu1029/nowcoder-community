package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.SensitiveBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper mapper;

    @Autowired
    private SensitiveBuilder sensitiveBuilder;

    /**
     * 根据用户id实现对帖子的分页查询，如果id=0，则不限用户
      * @param userId 用户id
     * @param offset 起始查询相对数据库第一条记录的偏移量
     * @param limit 每页限定的记录数
     * @return 查询到的帖子的集合
     */
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return mapper.selectDiscussPosts(userId, offset, limit);
    }

    /**
     * 通过指定用户id的帖子数量。如果id=0，则查询所有帖子的数量
     * @param userId 用户id
     * @return 查询到的记录数，即帖子数量
     */
    public int findDiscussPostRows(int userId) {
        return mapper.selectDiscussPostRows(userId);
    }

    /**
     * 向数据库中插入新帖子，需要先进行html标签转义以及敏感词过滤
     * @param post 新帖子
     * @return 插入的记录数
     */
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 如果内容带有html标签，可能会对页面造成影响，因此需要调用spring的工具对标签进行转义
        // 对标题中的标签进行转义
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        // 对内容中的标签进行转义
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        // 过滤标题和内容的敏感词
        post.setTitle(sensitiveBuilder.filter(post.getTitle()));
        post.setContent(sensitiveBuilder.filter(post.getContent()));

        // 向数据库插入数据
        mapper.insertDiscussPost(post);

        return 0;
    }

    /**
     * 根据id查找帖子
     * @param id 帖子id
     * @return 帖子
     */
    public DiscussPost findDiscussPostById(int id) {
        return mapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return mapper.updateCommentCount(id, commentCount);
    }
}
