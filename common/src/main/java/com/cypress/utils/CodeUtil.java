package com.cypress.utils;

import java.util.Random;

public class CodeUtil {
    /**
     * 生成指定长度的数字验证码
     * @param length 验证码长度，默认为6位
     * @return 随机数字验证码
     */
    public static String generateCode(int length) {
        if (length <= 0) {
            length = 6; // 默认6位
        }

        Random random = new Random();
        // 生成0到10^length-1范围内的随机数
        int max = (int) Math.pow(10, length) - 1;
        int code = random.nextInt(max + 1);

        // 格式化为指定长度的字符串，不足位数的前面补0
        return String.format("%0" + length + "d", code);
    }

    /**
     * 生成6位数字验证码
     * @return 6位随机数字验证码
     */
    public static String generateCode() {
        return generateCode(6);
    }
}
