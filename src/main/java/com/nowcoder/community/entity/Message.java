package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private int id;     // 私信消息id
    private int fromId; // 发送者id
    private int toId;   // 接收者id
    private String conversationId;  // 会话id
    private String content;
    private int status; // 状态。0：未读，1：已读，2：删除
    private Date createTime;
}
