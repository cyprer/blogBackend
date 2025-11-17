package com.cypress.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class PasswordEncoder {
    // 单例模式：BCryptPasswordEncoder 实例（线程安全，可全局复用）
    private static final BCryptPasswordEncoder BCRYPT_ENCODER = new BCryptPasswordEncoder();

    /**
     * 对原始密码进行 BCrypt 加密（自动生成随机盐值）
     * @param rawPassword 原始明文密码（不可为 null 或空串）
     * @return 加密后的密码（包含盐值，长度固定 60 字符）
     * @throws IllegalArgumentException 若原始密码为空
     */
    public static String encode(String rawPassword) {
        // 严格校验输入：避免空密码
        Assert.hasText(rawPassword, "原始密码不能为空");
        return BCRYPT_ENCODER.encode(rawPassword);
    }

    /**
     * 验证原始密码与加密密码是否匹配
     * @param rawPassword 原始明文密码
     * @param encodedPassword 加密后的密码（从数据库获取）
     * @return true：匹配；false：不匹配（包括参数为空的情况）
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        // 若任一参数为空，直接返回不匹配
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        try {
            return BCRYPT_ENCODER.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            // 避免加密字符串格式错误导致的异常（如数据库中密码被篡改）
            return false;
        }
    }

    // 私有构造方法：禁止实例化工具类
    private PasswordEncoder() {}
}