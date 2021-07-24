package com.nowcoder.community.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LoginTicket {
    private int id;         // 在数据库中的主键
    private int userId;     // 用户id
    private String ticket;  // 用户的凭据，放在cookie中
    private int status;     // 凭证是否有效，0：有效，1：无效
    private Date expired;   // 过期时间
}
