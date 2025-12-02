package com.cypress.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

    /**
     * 用户ID - 唯一标识
     * 使用String类型避免JavaScript中Number精度问题
     */
    private String userId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户名 - 可重复
     */
    private String username;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 性别：0-未知 1-男 2-女
     */
    private Integer gender;

    /**
     * 头像url
     */
    private String avatarUrl;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}