package com.cypress.persistence.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPo {

    /**
     * 主键id
     */
    private long id;

    /**
     * 用户ID - 唯一标识
     */
    private Long userId;

    /**
     * 手机号 - 唯一标识
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

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
     * 状态：0-禁用 1-正常
     */
    private Integer status;

    /**
     * 角色：0-普通用户 1-管理员
     */
    private Integer role;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;
}
