package com.cypress.user.service;

import com.cypress.dto.RegisterDto;
import com.cypress.dto.UserInfo;
import com.cypress.enums.VerificationResult;
import com.cypress.request.UpdateUserInfoRequest;
import com.cypress.response.Response;
import com.cypress.user.repository.IUserRepository;
import com.cypress.utils.CodeUtil;
import com.cypress.utils.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.cypress.user.model.entity.User;


import java.util.List;
import java.time.LocalDateTime;

/**
 * 用户领域服务 - 领域层
 * 处理用户相关的核心业务逻辑
 */

@Slf4j
@Service
public class UserDomainService implements IUserDomainService{


    @Autowired
    private IUserRepository userRepository;
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
     * @param phone
     * @param code
     * @return
     */
    @Override
    public Response<User> register(String phone, String code) {
        // 验证手机号格式
        if (!isValidPhone(phone)) {
            return Response.<User>builder()
                    .code("400")
                    .info("手机号格式不正确")
                    .build();
        }

        // 验证验证码
        if (code == null || code.isEmpty()) {
            return Response.<User>builder()
                    .code("400")
                    .info("验证码不能为空")
                    .build();
        }

        VerificationResult result = validCode(phone, code);
        if (result != VerificationResult.SUCCESS) {
            return Response.<User>builder()
                    .code(result.getCode())
                    .info(result.getInfo())
                    .build();
        }

        // 检查手机号是否已注册
        User existingUser = userRepository.findByPhone(phone);
        if (existingUser != null) {
            return Response.<User>builder()
                    .code("400")
                    .info("手机号已被注册")
                    .build();
        }

        // 创建新用户
        User newUser = new User();
        newUser.register(phone);

        // 保存用户
        newUser = userRepository.save(newUser);
        
        if (newUser == null) {
            return Response.<User>builder()
                    .code("500")
                    .info("注册失败：无法创建用户")
                    .build();
        }

        return Response.<User>builder()
                .code("200")
                .info("注册成功")
                .data(newUser)
                .build();
    }

    /**
     * 通过密码登录
     * @param loginKey 登录标识（手机号/用户名/邮箱）
     * @param password 密码
     * @return 登录用户
     */
    @Override
    public Response<User> loginByPassword(String loginKey, String password) {
        User user = null;
        // 根据登录标识类型查找用户
        if (loginKey.matches("^1[3-9]\\d{9}$")) {
            // 手机号登录
            user = userRepository.findByPhone(loginKey);
        } else if (loginKey.contains("@")) {
            // 邮箱登录
            user = userRepository.findByEmail(loginKey);
        } else {
            // 用户名登录
            List<User> users = userRepository.findAllByUsername(loginKey);
            if (users.size() == 1) {
                user = users.get(0);
            } else if (users.size() > 1) {
                // 多个同名用户，需要通过密码匹配找到正确的用户
                user = findUserByPassword(users, password);
            }
        }

        // 检查用户是否存在
        if (user == null) {
            return Response.<User>builder()
                    .code("400")
                    .info("用户不存在")
                    .build();
        }

        // 检查密码是否正确
        if (!PasswordEncoder.matches(password, user.getPassword())) {
            return Response.<User>builder()
                    .code("400")
                    .info("密码错误")
                    .build();
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.update(user);

        return Response.<User>builder()
                .code("200")
                .info("登录成功")
                .data(user)
                .build();
    }

    /**
     * 通过验证码登录
     * @param phone 手机号
     * @param code 验证码
     * @return 登录用户
     */
    @Override
    public Response<User> loginByCode(String phone, String code) {
        // 验证手机号格式
        if (!isValidPhone(phone)) {
            return Response.<User>builder()
                    .code("400")
                    .info("手机号格式不正确")
                    .build();
        }

        // 验证验证码
        if (code == null || code.isEmpty()) {
            return Response.<User>builder()
                    .code("400")
                    .info("验证码不能为空")
                    .build();
        }
        VerificationResult result = validCode(phone, code);
        if (result != VerificationResult.SUCCESS) {
            return Response.<User>builder()
                    .code(result.getCode())
                    .info(result.getInfo())
                    .build();
        }
        User user = userRepository.findByPhone(phone);
        if (user == null) {
            return Response.<User>builder()
                    .code("400")
                    .info("用户不存在")
                    .build();
        }
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.update(user);
        return Response.<User>builder()
                .code("200")
                .info("登录成功")
                .data(user)
                .build();

    }

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @return 验证结果
     */
    @Override
    public VerificationResult validCode(String phone, String code) {
        return userRepository.validCode(phone, code);
    }

    /**
     * 设置用户密码
     * @param userId 用户ID
     * @param password 密码
     * @return 设置成功的用户
     */
    @Override
    public Response<User> setPassword(Long userId, String password) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return Response.<User>builder()
                    .code("400")
                    .info("用户不存在")
                    .build();
        }

        if (!StringUtils.hasText(password)) {
            return Response.<User>builder()
                    .code("400")
                    .info("密码不能为空")
                    .build();
        }

        String encodedPassword = PasswordEncoder.encode(password);
        user.setPassword(encodedPassword);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.update(user);
        return Response.<User>builder()
                .code("200")
                .info("设置密码成功")
                .build();
    }

    /**设置用户手机号
     * @param userId
     * @param phone
     * @return
     */
    @Override
    public Response<String> setPhone(Long userId, String phone) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return Response.<String>builder()
                    .code("400")
                    .info("用户不存在")
                    .build();
        }
        user.setPhone(phone);
        userRepository.update(user);
        return Response.<String>builder()
                .code("200")
                .info("设置手机号成功")
                .data(phone)
                .build();
    }

    /**获取用户信息
     * @param userId
     * @return
     */
    @Override
    public Response<User> getUserInfo(Long userId) {
        User user = userRepository.findByUserId(userId);
        if (user != null) {
            return Response.<User>builder()
                    .code("200")
                    .info("获取用户信息成功")
                    .data(user)
                    .build();
        } else {
            return Response.<User>builder()
                    .code("400")
                    .info("用户不存在")
                    .build();
        }
    }

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param updateRequest 更新请求
     * @return 更新后的用户
     */
    @Override
    public Response<User> updateUserInfo(Long userId, UpdateUserInfoRequest updateRequest) {
        // 检查用户是否存在
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return Response.<User>builder()
                    .code("400")
                    .info("用户不存在")
                    .build();
        }

        // 如果请求中包含新的userId，则也需要转换并检查是否已被占用
        Long newUserId = null;
        if (updateRequest.getUserId() != null && !updateRequest.getUserId().isEmpty()) {
            try {
                newUserId = Long.parseLong(updateRequest.getUserId());
                
                // 检查新的userId是否已被其他用户使用
                User userWithNewId = userRepository.findByUserId(newUserId);
                if (userWithNewId != null) {
                    return Response.<User>builder()
                            .code("400")
                            .info("用户ID已被占用")
                            .build();
                }
            } catch (NumberFormatException e) {
                return Response.<User>builder()
                        .code("400")
                        .info("新用户ID格式不正确")
                        .build();
            }
        }

        // 检查邮箱是否已被其他用户使用
        if (StringUtils.hasText(updateRequest.getEmail())) {
            User userWithEmail = userRepository.findByEmail(updateRequest.getEmail());
            if (userWithEmail != null && !userWithEmail.getUserId().equals(userId)) {
                return Response.<User>builder()
                        .code("400")
                        .info("邮箱已被其他用户使用")
                        .build();
            }
            user.setEmail(updateRequest.getEmail());
        }

        if (StringUtils.hasText(updateRequest.getUsername())) {
            user.setUsername(updateRequest.getUsername());
        }
        if (updateRequest.getAge() != null) {
            user.setAge(updateRequest.getAge());
        }
        if (updateRequest.getGender() != null) {
            user.setGender(updateRequest.getGender());
        }
        if (updateRequest.getAvatarUrl() != null) {
            user.setAvatarUrl(updateRequest.getAvatarUrl());
        }
        if (updateRequest.getBio() != null) {
            user.setBio(updateRequest.getBio());
        }
        if (updateRequest.getSignature() != null) {
            user.setSignature(updateRequest.getSignature());
        }

        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());

        // 如果有新的userId，需要特殊处理
        if (updateRequest.getUserId() != null && !updateRequest.getUserId().isEmpty()) {
            // 先使用普通更新方法更新所有其他字段（此时userId还是旧的）
            user = userRepository.update(user);

            // 然后单独更新userId
            user = userRepository.updateUserId(user.getId(), newUserId);
        } else {
            // 没有更新userId，使用普通更新方法
            user = userRepository.update(user);
        }

        return Response.<User>builder()
                .code("200")
                .info("更新用户信息成功")
                .data(user)
                .build();
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
            if (PasswordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}