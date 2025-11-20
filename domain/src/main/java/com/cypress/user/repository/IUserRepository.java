package com.cypress.user.repository;

import com.cypress.enums.VerificationResult;
import com.cypress.user.model.entity.User;

import java.util.List;

/**
 * 用户仓库接口 - 领域层
 * 定义领域对用户数据的访问规范
 */
public interface IUserRepository {
    /**
     * 根据手机号查找用户
     * @param phone 手机号
     * @return 用户实体
     */
    User findByPhone(String phone);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户实体列表（用户名可重复）
     */
    User findByUsername(String username);

    /**
     * 根据用户名查找所有匹配的用户
     * @param username 用户名
     * @return 用户实体列表
     */
    List<User> findAllByUsername(String username);

    /**
     * 保存用户
     * @param user 用户实体
     * @return 保存后的用户
     */
    User save(User user);

    /**
     * 根据ID查找用户
     * @param userId 用户ID
     * @return 用户实体
     */
    User findByUserId(Long userId);

    /**
     * 更新用户
     * @param user 用户实体
     * @return 更新后的用户
     */
    User update(User user);

    /**
     * 保存验证码
     * @param phone 手机号
     * @param code 验证码
     */
    void saveCode(String phone, String code);

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 验证结果
     */
    VerificationResult validCode(String phone, String code);

    /**
     * 根据邮箱查找用户
     * @param email 邮箱
     * @return 用户实体
     */
    User findByEmail(String email);

}
