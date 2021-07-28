package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * 处理账号设置
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    /**
     * 请求设置页面
     *
     * @return 设置页面
     */
    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage() {
        return "site/setting";
    }

    /**
     * 上传头像
     *
     * @param headerImg 头像图片文件
     * @param model     model
     * @return 如果上传成功，重定向到首页；否则，返回到设置页面
     */
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImg, Model model) {
        if (headerImg == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "site/setting";
        }

        // 得到文件名
        String filename = headerImg.getOriginalFilename();
        // 得到后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确");
            return "site/setting";
        }

        // 生成随机的文件名
        filename = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放路径
        File dest = new File(uploadPath + "/" + filename);
        // 将当前文件写到指定路径中
        try {
            headerImg.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常", e);
        }

        // 更新当前用户的头像路径（web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        userService.updateHeader(user.getId(), headerUrl);

        // 重定向回首页
        return "redirect:/index";
    }

    /**
     * 浏览器上打印头像图片
     *
     * @param filename 图片文件名
     * @param response 响应
     */
    @GetMapping("/header/{filename}")
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        // 服务器存放文件的路径
        filename = uploadPath + "/" + filename;
        // 获取文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix); // 类型为image且后缀为suffix

        FileInputStream fis = null;
        try {
            ServletOutputStream os = response.getOutputStream();
            // 输入流是自己创建的，不归springMvc管理，要自己手动关闭
            fis = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @LoginRequired
    @PostMapping("/updatePassword")
    public String updatePassword(String originPassword, String password,
                                 Model model, @CookieValue("ticket") String ticket) {
        // 输入密码为空
        if (StringUtils.isBlank(password)) {
            model.addAttribute("passwordEqualsError", "新密码不能为空");
            return "site/setting";
        }

        User user = hostHolder.getUser();
        Map<String, String> map = userService.updatePassword(user.getId(), originPassword, password);
        // 将错误信息加入model中
        String originErr = map.get("originPasswordError");
        String passwordErr = map.get("passwordEqualsError");
        // 如果出错，则返回设置页面
        if (originErr != null || passwordErr != null) {
            model.addAttribute("originPasswordError", originErr);
            model.addAttribute("passwordEqualsError", passwordErr);
            return "site/setting";
        }

        // 密码已更新，需要使用户退出登录
        return "redirect:/logout";
    }

    /**
     * 访问用户的个人主页
     * @param userId 用户id
     * @param model model
     * @return 个人主页
     */
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("当前用户不存在");
        }

        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        long likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        return "/site/profile";
    }
}
