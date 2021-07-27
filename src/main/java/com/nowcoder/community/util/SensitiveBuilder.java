package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 用前缀树实现敏感词过滤
 */
@Component
public class SensitiveBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveBuilder.class);

    // 敏感词替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init() {
        InputStream is = null;
        BufferedReader reader = null;

        try {
            is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            reader = new BufferedReader(new InputStreamReader(is));

            String keyword;
            while ((keyword = reader.readLine()) != null) {
                // 添加敏感词
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词失败" + e.getMessage());
        }
    }

    // 将一个敏感词添加到前缀树中
    private void addKeyword(String keyword) {
        if (keyword.length() == 0) {
            return;
        }

        TrieNode p = root;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);

            TrieNode child = p.getChild(c);
            if (child == null) {
                child = new TrieNode();
                p.addChildren(c, child);
            }

            p = child;
        }

        p.setKeyWordEnd(true);
    }

    // 将过滤敏感词后的字符串返回
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        StringBuilder res = new StringBuilder();

        int i = 0;
        while (i < text.length()) {
            // 如果是特殊符号，跳过
            if (isSymbol(text.charAt(i))) {
                res.append(text.charAt(i++));
                continue;
            }

            boolean hasKeyword = false;
            TrieNode p = root;

            for (int j = i; j < text.length(); j++) {
                char c = text.charAt(j);
                // 如果是特殊符号，跳过
                if (isSymbol(c)) {
                    continue;
                }

                TrieNode child = p.getChild(c);
                if (child == null) {
                    break;
                }

                if (child.isKeyWordEnd) {
                    hasKeyword = true;
                    res.append(REPLACEMENT);
                    i = j + 1;
                    break;
                }

                p = child;
            }

            if (!hasKeyword) {
                res.append(text.charAt(i++));
            }
        }

        return res.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c) {
        return (c < 0x2E80 || c > 0x9FFF) && !CharUtils.isAsciiAlphanumeric(c);
    }

    // 前缀树结点
    private class TrieNode {
        // 是否是一个敏感词的结尾
        private boolean isKeyWordEnd;
        private Map<Character, TrieNode> children;

        public TrieNode() {
            isKeyWordEnd = false;
            children = new HashMap<>();
        }

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        public Map<Character, TrieNode> getChildren() {
            return children;
        }

        public void addChildren(Character c, TrieNode node) {
            children.put(c, node);
        }

        public TrieNode getChild(Character c) {
            return children.get(c);
        }
    }

}
