package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断拦截的目标是不是一个方法（可能还是静态资源）
        if (handler instanceof HandlerMethod) {
            // 向下转型并获得拦截到的方法
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 获取方法上的LoginRequired注解
            LoginRequired annotation = method.getAnnotation(LoginRequired.class);
            // 如果有注解，证明需要登录才能访问，如果发现用户没登录，则拦截这个请求，并重定向至登录页面
            if (annotation != null && hostHolder.getUser() == null) {
                // 由于这不是一个Controller，需要用调用response的重定向api完成重定向
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }

        return true;
    }
}
