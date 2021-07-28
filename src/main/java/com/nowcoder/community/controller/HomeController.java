package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // 处理访问首页的请求
    @GetMapping("/index")
    public String getIndexPage(Model model, Page page) {
        page.setRows(discussPostService.findDiscussPostRows(0));    // 设置总行数
        page.setPath("/index"); // 设置路径
        // page.current默认是1, page.limit默认为10。这两个参数都可以在请求url中设置
        model.addAttribute("page", page);

        // 从数据库中取出当前页面显示范围里的数据
        List<DiscussPost> posts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());

        // 将帖子和用户信息一起打包进一个map列表，返回给前端
        List<Map<String, Object>> postAndUser = new ArrayList<>();
        if (posts != null) {
            for (DiscussPost post : posts) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                map.put("user", userService.findUserById(post.getUserId()));    // 得到相应的用户信息
                // 帖子的赞数
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                postAndUser.add(map);
            }
        }

        model.addAttribute("discussPosts", postAndUser);

        return "index";
    }

    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }
}
