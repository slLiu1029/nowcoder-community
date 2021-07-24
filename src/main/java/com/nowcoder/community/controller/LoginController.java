package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    // 日志
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 请求注册页面
     *
     * @return 注册页面
     */
    @GetMapping("/register")
    public String getRegisterPage() {
        return "site/register";
    }

    /**
     * 处理注册post请求
     *
     * @param model model
     * @param user  封装注册框中填写的用户名和密码
     * @return 如果注册成功，则返回跳转提示页面；否则重新返回注册页面
     */
    @PostMapping("/register")
    public String register(Model model, User user) {
        model.addAttribute("user", user);

        // userService返回错误信息，如果没有错误，则填充新用户信息和验证激活信息
        Map<String, Object> map = userService.register(user);

        // 注册成功，没有错误信息，返回跳转提示页面
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }

        // 注册出现错误，在model里添加错误信息，并重新返回到注册页面
        model.addAttribute("usernameMsg", map.get("usernameMsg"));
        model.addAttribute("passwordMsg", map.get("passwordMsg"));
        model.addAttribute("emailMsg", map.get("emailMsg"));
        return "/site/register";
    }

    /**
     * 处理激活请求
     *
     * @param model  model
     * @param userId url中userId参数
     * @param code   url中code参数
     * @return 激活成功，返回登陆页面；否则返回到主页
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);

        if (result == ACTIVATION_SUCCESS) {
            // 激活成功，跳转到主页
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
        } else if (result == ACTIVATION_REPEAT) {
            // 该用户已被激活，跳转到主页
            model.addAttribute("msg", "无效操作，该账号已经被激活了！");
            model.addAttribute("target", "/index");
        } else {
            // 验证码不正确，跳转到主页
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }

        return "site/operate-result";
    }

    /**
     * 获取登陆页面
     *
     * @return 登陆页面
     */
    @GetMapping("/login")
    public String getLoginPage() {
        return "site/login";
    }

    /**
     * 获取验证码
     *
     * @param response http响应，用于将图片打印在浏览器页面上
     * @param session  session会话，用于存放验证码文本
     */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session
        session.setAttribute("kaptcha", text);

        // 将图片输出给浏览器，参数是类型：image且格式是png
        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败" + e.getMessage());
        }
    }

    /**
     * 处理登录请求。首先先验证验证码是否正确，然后调用业务层登录方法，将凭证存入cookie中，随着响应报文发送给客户端
     * @param username 输入的用户名
     * @param password 输入的密码
     * @param code 输入的验证码
     * @param rememberMe 是否勾选”记住我“
     * @param model model
     * @param session session，存放正确的验证码
     * @param response 响应
     * @return 如果登录失败，重新回到登陆页面；否则，重定向到首页
     */
    @PostMapping("/login")
    public String login(String username, String password, String code, boolean rememberMe,
                        Model model, HttpSession session, HttpServletResponse response) {
        // 将参数加入model中
        model.addAttribute("username", username);
        model.addAttribute("password", password);
        model.addAttribute("code", code);
        model.addAttribute("rememberMe", rememberMe);

        String kaptcha = (String) session.getAttribute("kaptcha");  // 获取session里的验证码
        // 检查验证码
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确");
            return "site/login";
        }

        // 检查账号密码
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        // 生成登录凭证
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        // 只有map里包含ticket这个key，证明登陆成功了
        if (map.containsKey("ticket")) {
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);    // cookie应作用于所有页面
            cookie.setMaxAge(expiredSeconds);   // 设置cookie有效时间
            response.addCookie(cookie);     // 响应报文带上cookie

            return "redirect:index";        // 登录成功，重定向到首页
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));

            return "site/login";    // 登录失败，回到登陆页面
        }
    }

    /**
     * 处理登出请求
     * @param ticket cookie中的登录凭证
     * @return 重定向到首页
     */
    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:index";
    }
}
