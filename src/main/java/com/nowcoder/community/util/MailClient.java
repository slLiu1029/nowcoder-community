package com.nowcoder.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 发送邮件的邮箱客户端
 */
@Component
public class MailClient {

    private static final Logger logger;

    @Autowired
    private JavaMailSender mailSender;  // springboot自动注入

    @Value("${spring.mail.username}")
    private String from;

    static {
        logger = LoggerFactory.getLogger(MailClient.class);
    }

    /**
     * 将邮件发送给目标邮箱
     * @param to 收件邮箱地址
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            System.out.println("邮件发送失败" + e.getMessage());
        }
    }

}
