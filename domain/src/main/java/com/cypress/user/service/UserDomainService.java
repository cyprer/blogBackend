package com.cypress.user.service;

import com.cypress.enums.VerificationResult;
import com.cypress.response.Response;
import com.cypress.user.repository.IUserRepository;
import com.cypress.utils.CodeUtil;
import com.cypress.utils.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.cypress.user.model.entity.User;
import java.util.List;

/**
 * 用户领域服务 - 领域层
 * 处理用户相关的核心业务逻辑
 */

@Slf4j
@Service
public class UserDomainService implements IUserDomainService{


    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     * 发送验证码
     * @param phone 手机号
     * @return 验证码
     */

    @Override
    public String sendCode(String phone) {
        String code = CodeUtil.generateCode();
        log.info("发送验证码：{}", code);
        userRepository.saveCode(phone, code);
        return code;
    }


    /**
     * 用户注册
     * @param phone 手机号
     * @param password 密码
     * @return 注册成功的用户
     */
    @Override
    public Response<User> register(String phone, String password) {
        // 验证手机号格式
        if (!StringUtils.hasText(phone)) {
            return Response.<User>builder()
                    .code("400")
                    .info("手机号不能为空")
                    .build();
        }
        if (!isValidPhone(phone)) {
            return Response.<User>builder()
                    .code("400")
                    .info("手机号格式不正确")
                    .build();
        }

        // 验证密码
        if (!StringUtils.hasText(password)) {
            return Response.<User>builder()
                    .code("400")
                    .info("密码不能为空")
                    .build();
        }
        if (password.length() < 6) {
            return Response.<User>builder()
                    .code("400")
                    .info("密码长度不能少于6位")
                    .build();
        }

        // 检查手机号是否已注册
        User existingUser = userRepository.findByPhone(phone);
        if (existingUser != null) {
            return Response.<User>builder()
                    .code("400")
                    .info("该手机号已注册")
                    .build();
        }

        // 加密密码
        String encodedPassword = passwordEncoder.encode(password);

        // 创建用户实体
        User user = new User();
        user.register(phone, encodedPassword);

        // 保存用户
        User savedUser = userRepository.save(user);
        return Response.<User>builder()
                .code("200")
                .info("注册成功")
                .data(savedUser)
                .build();
    }

    /**
     * 用户登录
     * @param loginKey 登录凭证（手机号或用户名）
     * @param password 密码
     * @return 登录成功的用户
     */
    @Override
    public Response<User> login(String loginKey, String password) {
        // 验证参数
        if (!StringUtils.hasText(loginKey)) {
            return Response.<User>builder()
                    .code("400")
                    .info("登录凭证不能为空")
                    .build();
        }
        if (!StringUtils.hasText(password)) {
            return Response.<User>builder()
                    .code("400")
                    .info("密码不能为空")
                    .build();
        }

        User user = null;
        
        // 根据登录凭证类型查找用户
        if (isValidPhone(loginKey)) {
            // 使用手机号登录
            user = userRepository.findByPhone(loginKey);
        } else {
            // 使用用户名登录
            List<User> users = userRepository.findAllByUsername(loginKey);
            
            // 如果没有找到用户
            if (users.isEmpty()) {
                user = null;
            }
            // 如果只找到一个用户，直接使用
            else if (users.size() == 1) {
                user = users.get(0);
            }
            // 如果找到多个同名用户，需要通过密码进一步验证
            else {
                user = findUserByPassword(users, password);
            }
        }

        // 验证用户是否存在
        if (user == null) {
            return Response.<User>builder()
                    .code("400")
                    .info("用户不存在或密码错误")
                    .build();
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Response.<User>builder()
                    .code("400")
                    .info("用户不存在或密码错误")
                    .build();
        }

        return Response.<User>builder()
                .code("200")
                .info("登录成功")
                .data(user)
                .build();
    }

    /**
     * 更新用户名
     * @param userId 用户ID
     * @param newUsername 新用户名
     * @return 更新后的用户
     */
    @Override
    public Response<User> updateUsername(Long userId, String newUsername) {
        if (userId == null) {
            return Response.<User>builder()
                    .code("400")
                    .info("用户ID不能为空")
                    .build();
        }
        if (!StringUtils.hasText(newUsername)) {
            return Response.<User>builder()
                    .code("400")
                    .info("用户名不能为空")
                    .build();
        }

        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.<User>builder()
                    .code("400")
                    .info("用户不存在")
                    .build();
        }

        user.updateUsername(newUsername);
        User updatedUser = userRepository.update(user);
        return Response.<User>builder()
                .code("200")
                .info("用户名更新成功")
                .data(updatedUser)
                .build();
    }

    @Override
    public VerificationResult validCode(String phone, String code) {
        return userRepository.validCode(phone, code);
    }

    /**
     * 验证手机号格式
     * @param phone 手机号
     * @return 是否有效
     */
    private boolean isValidPhone(String phone) {
        // 简单的手机号格式验证
        return phone.matches("^1[3-9]\\d{9}$");
    }
    
    /**
     * 通过密码匹配从同名用户中找到正确的用户
     * @param users 同名用户列表
     * @param password 密码
     * @return 匹配的用户，如果找不到则返回null
     */
    private User findUserByPassword(List<User> users, String password) {
        for (User user : users) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}