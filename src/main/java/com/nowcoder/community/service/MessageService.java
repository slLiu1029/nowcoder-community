package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.SensitiveBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveBuilder sensitiveBuilder;

    /**
     * 查询某个用户的所有会话，每个会话显示最新一条消息
     * @param userId 用户id
     * @param offset 分页起始
     * @param limit 每天显示数量
     * @return 指定用户所有会话的最新消息
     */
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    /**
     * 查询指定用户的会话数量
     * @param userId 用户id
     * @return 会话数量
     */
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    /**
     * 分页查询指定会话的消息
     * @param conversationId 会话id
     * @param offset 起始偏移量
     * @param limit 每页记录数
     * @return 指定会话的消息
     */
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    /**
     * 查询指定会话的消息数
     * @param conversationId 会话id
     * @return 会话消息数量
     */
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    /**
     * 查询指定用户的指定会话的未读消息数
     * @param userId 用户id
     * @param conversationId 会话id
     * @return 指定用户的指定会话的未读消息数
     */
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    /**
     * 添加私信
     * @param message 私信
     * @return 成功添加的私信数量
     */
    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveBuilder.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    /**
     * 将指定id的私信集合设为已读
     * @param ids 私信id几黑
     * @return 从未读变成已读状态的私信数量
     */
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }

    /**
     * 删除指定id的私信（把私信状态设为2）
     * @param id
     * @return
     */
    public int deleteMessage(int id) {
        return messageMapper.updateStatus(new ArrayList<Integer>(id), 2);
    }
}
