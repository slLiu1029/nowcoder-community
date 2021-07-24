package com.nowcoder.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    // 生成随机字符串，用于激活码等用途
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // MD5加密
    // 先在原密码追加一个随机的字符串，然后进行加密，进一步加强安全性
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) { // key是null或者空串都会判定为true
            return null;
        }
        // 加密成16进制
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
