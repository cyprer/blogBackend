package com.cypress.user.model.entity;

import com.cypress.constants.Constants;
import com.cypress.utils.SnowflakeIdGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体 - 领域层
 * 包含用户的核心业务属性和行为
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

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

    /**
     * 注册用户
     * @param phone 手机号
     * @param password 密码
     */
    public void register(String phone, String password) {
        this.userId = SnowflakeIdGenerator.generateId();
        this.phone = phone;
        this.password = password;
        this.email = null;
        this.username = generateDefaultUsername(phone);
        this.age = 18;
        this.gender = 0;
        this.avatarUrl = "";
        this.bio = "";
        this.signature = "";
        this.status = 1;
        this.role = 0; // 默认普通用户
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.lastLoginTime = LocalDateTime.now();
    }


    /**
     * 生成默认用户名
     * @param phone 手机号
     * @return 默认用户名
     */
    private String generateDefaultUsername(String phone) {
        // 使用手机号后4位作为默认用户名
        return Constants.UserConstants.USER + phone.substring(phone.length() - 4);
    }

    /**
     * 更新用户名
     * @param username 新用户名
     */
    public void updateUsername(String username) {
        this.username = username;
        this.updateTime = LocalDateTime.now();
        this.lastLoginTime = LocalDateTime.now();
    }

}