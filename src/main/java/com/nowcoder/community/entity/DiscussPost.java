package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class DiscussPost {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;   // 0表示普通，1表示置顶
    private int status; // 帖子的状态. 0：正常  1：精华  2：拉黑
    private Date createTime;
    private int commentCount;
    private double score;
}
