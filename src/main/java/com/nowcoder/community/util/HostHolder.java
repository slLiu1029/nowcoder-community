package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息（代替session），且保证线程隔离
 */
@Component
public class HostHolder {
    // 实现线程隔离，users里的对象都是根据线程隔离的
    private ThreadLocal<User> users = new ThreadLocal<>();

    // 将user加入users
    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
