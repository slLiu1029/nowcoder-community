package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class) // 和CommunityApplication共享配置类
public class MailTests {
//    @Autowired
//    private MailClient mailClient;
//
//    @Test
//    public void testTextMail() {
//        mailClient.sendMail("mail.ustc.edu.cn", "Hello!", "This is a test email!");
//    }
}
