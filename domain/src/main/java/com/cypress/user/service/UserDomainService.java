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
        // 调用领域服务进行注册
        VerificationResult result = validCode(phone, code);
        if (result != VerificationResult.SUCCESS) {
            return Response.<User>builder()
                    .code(result.getCode())
                    .info(result.getInfo())
                    .build();
        }
        User user = new User();
        user.register(phone);
        // 使用默认密码进行加密

        String encodedPassword = PasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        return Response.<User>builder()
                .code("200")
                .info("注册成功")
                .data(user)
                .build();
    }

    /**
     * 用户登录
     * @param loginKey 登录凭证（手机号或用户名）
     * @param password 密码
     * @return 登录成功的用户
     */
    @Override
    public Response<User> loginByPassword(String loginKey, String password) {
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
                    .info("用户不存在")
                    .build();
        }
        
        // 验证密码
        if (!PasswordEncoder.matches(password, user.getPassword())) {
            return Response.<User>builder()
                    .code("400")
                    .info("密码错误")
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
     * @param phone
     * @param code
     * @return
     */
    @Override
    public Response<User> loginByCode(String phone, String code) {
        // 验证参数
        if (!StringUtils.hasText(phone)) {
            return Response.<User>builder()
                    .code("400")
                    .info("手机号不能为空")
                    .build();
        }
        if (!StringUtils.hasText(code)) {
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
        // 验证参数
        if (userId == null) {
            return Response.<User>builder()
                    .code("400")
                    .info("用户ID不能为空")
                    .build();
        }
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

        // 查找用户
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return Response.<User>builder()
                    .code("400")
                    .info("用户不存在")
                    .build();
        }

        // 加密密码
        String encodedPassword = PasswordEncoder.encode(password);

        // 设置密码
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
        }
        return Response.<User>builder()
                .code("400")
                .info("用户不存在")
                .build();
    }

    /**
     * @param userId
     * @param updateUserInfoRequest
     * @return
     */
    @Override
    public Response<User> updateUserInfo(Long userId, UpdateUserInfoRequest updateUserInfoRequest) {
        // 验证用户是否存在
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            return Response.<User>builder()
                    .code("400")
                    .info("用户不存在")
                    .build();
        }

        // 验证要更新的userId是否已被占用（如果请求中包含新的userId）
        if (updateUserInfoRequest.getUserId() != null && !updateUserInfoRequest.getUserId().equals(userId)) {
            User existingUser = userRepository.findByUserId(updateUserInfoRequest.getUserId());
            if (existingUser != null) {
                return Response.<User>builder()
                        .code("400")
                        .info("用户ID已存在")
                        .build();
            }
        }

        // 验证用户名唯一性
        if (updateUserInfoRequest.getUsername() != null && !updateUserInfoRequest.getUsername().equals(user.getUsername())) {
            User existingUser = userRepository.findByUsername(updateUserInfoRequest.getUsername());
            if (existingUser != null) {
                return Response.<User>builder()
                        .code("400")
                        .info("用户名已存在")
                        .build();
            }
        }

        // 验证邮箱唯一性
        if (updateUserInfoRequest.getEmail() != null && updateUserInfoRequest.getEmail().trim().length() > 0) {
            if (!updateUserInfoRequest.getEmail().equals(user.getEmail())) {
                User existingUser = userRepository.findByEmail(updateUserInfoRequest.getEmail());
                if (existingUser != null) {
                    return Response.<User>builder()
                            .code("400")
                            .info("邮箱已存在")
                            .build();
                }
            }
        }

        // 部分更新：只更新非空字段
        if (updateUserInfoRequest.getUserId() != null) {
            user.setUserId(updateUserInfoRequest.getUserId());
        }
        if (updateUserInfoRequest.getEmail() != null && updateUserInfoRequest.getEmail().trim().length() > 0) {
            user.setEmail(updateUserInfoRequest.getEmail());
        }
        if (updateUserInfoRequest.getUsername() != null && updateUserInfoRequest.getUsername().trim().length() > 0) {
            user.setUsername(updateUserInfoRequest.getUsername());
        }
        if (updateUserInfoRequest.getAge() != null) {
            user.setAge(updateUserInfoRequest.getAge());
        }
        if (updateUserInfoRequest.getGender() != null) {
            user.setGender(updateUserInfoRequest.getGender());
        }
        if (updateUserInfoRequest.getAvatarUrl() != null && updateUserInfoRequest.getAvatarUrl().trim().length() > 0) {
            user.setAvatarUrl(updateUserInfoRequest.getAvatarUrl());
        }
        if (updateUserInfoRequest.getBio() != null && updateUserInfoRequest.getBio().trim().length() > 0) {
            user.setBio(updateUserInfoRequest.getBio());
        }
        if (updateUserInfoRequest.getSignature() != null && updateUserInfoRequest.getSignature().trim().length() > 0) {
            user.setSignature(updateUserInfoRequest.getSignature());
        }

        // 设置更新时间
        user.setUpdateTime(LocalDateTime.now());

        // 更新用户
        user = userRepository.update(user);
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