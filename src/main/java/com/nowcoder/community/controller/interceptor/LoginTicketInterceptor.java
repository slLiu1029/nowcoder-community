package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 拦截器，只有通过登录凭证的才能放行
 */
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    /**
     * 前置处理器，在Controller调用之前调用。拦截指定路径的请求，并从cookie中取出登录凭证ticket，并验证凭证是否通过.如果通过，则根据ticket
     * 提取用户信息，并把信息存进保证线程隔离的threadLocal中，请求放行
     * @param request 请求
     * @param response 响应
     * @param handler 处理器
     * @return 如果凭证有效，则返回true；否则，返回false
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket == null) {
            return true;
        }

        // 查询凭证
        LoginTicket loginTicket = userService.checkTicket(ticket);
        // 检查凭证是否有效
        // 无效情况：1.指定ticket不存在对应的loginTicket  2.loginTicket状态无效（比如浏览器已经退出登录了）  3.loginTicket过期
        if (loginTicket == null || loginTicket.getStatus() == 1 || loginTicket.getExpired().before(new Date())) {
            return true;
        }

        // 根据凭证查询用户
        User user = userService.findUserById(loginTicket.getUserId());
        // 存储user，且hostHolder使用threadLocal，使不同线程的user相互隔离，避免并发带来的同步问题
        hostHolder.setUser(user);

        return true;
    }

    /**
     * 后置拦截器，在Controller执行和模板引擎执行之间调用。将user信息添加进modelAndView属性中
     * @param request 请求
     * @param response 响应
     * @param handler 处理器
     * @param modelAndView 模型视图
     * @throws Exception 异常
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            modelAndView.addObject("letterUnreadCount", letterUnreadCount);
        }
    }

    /**
     * 请求执行完成后，用于清理资源
     * @param request 请求
     * @param response 响应
     * @param handler 处理器
     * @param ex 传入的异常
     * @throws Exception 抛出的异常
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
