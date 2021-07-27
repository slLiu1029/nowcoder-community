package com.nowcoder.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    // 声明切点：service包下所有类的所有方法
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut() {

    }

    // 前置通知
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        // 用户(ip)在什么时间访问了什么方法
        // 得到请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();    // 得到用户ip
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());    // 得到日期
        // 类名.方法名就是连接点（方法）的全名
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        // 将用户在某时间访问了某方法记录进日志
        logger.info(String.format("用户[%s]，在[%s]，访问了[%s].", ip, now, target));
    }
}
