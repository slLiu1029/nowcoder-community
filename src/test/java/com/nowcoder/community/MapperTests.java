package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);
    }

    // 测试DiscussPostMapper的方法
    @Test
    public void testSelectPosts() {
        List<DiscussPost> posts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost post : posts) {
            System.out.println(post);
        }

        System.out.println(discussPostMapper.selectDiscussPostRows(0));

        System.out.println();
        System.out.println("用户id 149的帖子");
        posts = discussPostMapper.selectDiscussPosts(149, 0, 10);
        for (DiscussPost post : posts) {
            System.out.println(post);
        }
        System.out.println(discussPostMapper.selectDiscussPostRows(149));
    }
}
