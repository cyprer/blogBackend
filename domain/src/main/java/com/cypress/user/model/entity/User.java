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

    public static String defaultRawPassword ="515221";

    /**
     * 主键ID - 数据库主键
     */
    private Long id = 0L;

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
     */
    public void register(String phone) {
        // id 字段由数据库自动生成，不需要在代码中设置
        this.userId = SnowflakeIdGenerator.generateId();
        this.phone = phone;
        this.password = defaultRawPassword;
        this.email = null;
        this.username = generateDefaultUsername(phone);
        this.age = 18;
        this.gender = 0;
        this.avatarUrl = null;
        this.bio = null;
        this.signature = null;
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

}