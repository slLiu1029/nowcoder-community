package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        // 此请求没有用户登录，需要返回403代码，表示没有权限
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还未登录");
        }

        DiscussPost post = new DiscussPost();
        post.setTitle(title);
        post.setContent(content);
        post.setUserId(user.getId());
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 0表示状态正常
        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    /**
     * 从url获取帖子id，将id传给业务层并得到对应的帖子，然后跳转到帖子详情页面
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int id, Model model, Page page) {
        // 帖子数据
        DiscussPost post = discussPostService.findDiscussPostById(id);
        model.addAttribute("post", post);

        // 作者信息
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        // 评论的分页信息
        page.setLimit(5);
        // 设置当前评论楼的路径
        page.setPath("/discuss/detail/" + id);
        page.setRows(post.getCommentCount());
        model.addAttribute("page", page);

        // 评论：直接给帖子的评论
        // 回复：评论帖子中评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 将评论信息和用户信息存进map中
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                // 此评论的回复列表，不做分页
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 回复同样也要Vo列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 处理回复的目标，只有点了回了评论楼中的某个人才会显示目标，直接回复帖子和评论楼不会有目标
                        // targetId=0表示没有target
                        User target = null;
                        int targetId = reply.getTargetId();
                        if (targetId != 0) {
                            target = userService.findUserById(targetId);
                        }
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                // 把回复楼装进commentVo中
                commentVo.put("replies", replyVoList);

                // 此评论楼里回复的数量，显示在评论的右下角
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }

}
