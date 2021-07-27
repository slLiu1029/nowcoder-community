package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private int id;
    private int userId;
    private int entityType; // 评论的实体类型，可以是帖子、课程等
    private int entityId;   // 评论实体的id
    private int targetId;   // 如果回复的是某层评论，则targetId就是被回复的那个人的id
    private String content;
    private int status;     // 0表示有效回复
    private Date createTime;
}
