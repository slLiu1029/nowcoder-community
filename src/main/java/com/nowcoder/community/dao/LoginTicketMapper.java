package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginTicketMapper {

    int insertLoginTicket(LoginTicket loginTicket);

    /**
     * 通过客户端的ticket数据可以知道是哪个用户
     * @param ticket 凭据
     * @return LoginTicket对象
     */
    LoginTicket selectByTicket(String ticket);

    /**
     * 改变指定ticket对应的login ticket的状态
     * @param ticket 凭据
     * @param status 状态
     * @return 修改结果
     */
    int updateStatus(String ticket, int status);

}
