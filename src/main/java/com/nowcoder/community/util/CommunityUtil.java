package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    /**
     * 生成随机字符串，用于激活码等用途
     * @return 生成的随机字符串
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 对输入的密码进行md5加密
     * @param key
     * @return
     */
    public static String md5(String key) {
        if (StringUtils.isBlank(key)) { // key是null或者空串都会判定为true
            return null;
        }
        // 加密成16进制
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 给浏览器返回json，包含各种信息
     * @param code 事先设定好的编码
     * @param msg 提示信息
     * @param map 封装key-value信息
     * @return json字符串
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    /**
     * 给浏览器返回json，包含各种信息
     * @param code 事先设定好的编码
     * @param msg 提示信息
     * @return json字符串
     */
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    /**
     * 给浏览器返回json，包含各种信息
     * @param code 事先设定好的编码
     * @return json字符串
     */
    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }
}
