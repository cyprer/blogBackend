package com.cypress.constants;

public class Constants {
    /**
     * Redis相关常量
     */
    public static class RedisConstants {
        /**
         * 验证码键前缀
         */
        public static final String VERIFICATION_CODE_PREFIX = "verification:code:";

        /**
         * 验证码有效期（毫秒）
         * 默认5分钟
         * 测试阶段先设置一个很大很大的值
         */
        public static final long VERIFICATION_CODE_EXPIRE =5 * 60 * 1000L;
    }

    /**
     * 用户相关常量
     */
    public static class UserConstants {
        /**
         * 默认用户名前缀
         */
        public static final String USER = "user";

    }
}
