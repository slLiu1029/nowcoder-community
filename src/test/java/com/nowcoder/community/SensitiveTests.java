package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveBuilder sensitiveBuilder;

    @Test
    public void testSensitiveBuilder() {
        String text = "  你个大 傻逼，快点滚😂蛋吧，别在这犯😅贱了哈哈哈";
        String s = sensitiveBuilder.filter(text);
        System.out.println(s);
    }

}
