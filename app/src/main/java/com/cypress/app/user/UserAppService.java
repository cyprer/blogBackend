package com.cypress.app.user;


import com.cypress.dto.UserInfo;
import com.cypress.request.*;
import com.cypress.dto.LoginDto;
import com.cypress.dto.RegisterDto;
import com.cypress.user.model.entity.User;
import com.cypress.user.service.IUserDomainService;
import com.cypress.utils.JwtUtil;
import com.cypress.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户应用服务 - 应用层
 * 作为控制器和领域服务之间的桥梁，负责协调领域对象完成业务操作
 */
@Slf4j
@Service
public class UserAppService {

    @Autowired
    private IUserDomainService userDomainService;

    @Autowired
    private JwtUtil jwtUtil;
    /**
     * 获取验证码
     * @param phone 手机号
     * @return 返回验证码
     */
    public Response<String> sendCode(String phone) {
        String code = userDomainService.sendCode(phone);
        return Response.<String>builder()
                .code("200")
                .info("发送验证码成功")
                .data(code)
                .build();
    }

    /**
     * 用户注册
     * @param request 注册请求
     * @return 用户响应
     */
    public Response<RegisterDto> register(RegisterRequest request) {
        Response<User> userResponse = userDomainService.register(
                request.getPhone(),
                request.getCode()
        );

        // 如果注册失败，直接返回错误响应
        if (!"200".equals(userResponse.getCode())) {
            return Response.<RegisterDto>builder()
                    .code(userResponse.getCode())
                    .info(userResponse.getInfo())
                    .build();
        }
        User user = userResponse.getData();
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        UserContext.setUserInfo(userInfo);
        // 注册成功，转换为RegisterDto
        RegisterDto registerDto = convertToResponse(userResponse.getData());
        return Response.<RegisterDto>builder()
                .code("200")
                .info("注册成功")
                .data(registerDto)
                .build();
    }

    /**
     * 通过密码登录
     * @param request 登录请求
     * @return 登录响应
     */
    public Response<LoginDto> loginByPassword(LoginByPasswordRequest request) {
        // 调用领域服务进行登录
        Response<User> userResponse = userDomainService.loginByPassword(
                request.getLoginKey(),
                request.getPassword()
        );
        
        return buildLoginResponse(userResponse);
    }

    /**
     * 通过验证码登录
     * @param request 登录请求
     * @return 登录响应
     */
    public Response<LoginDto> loginByCode(LoginByCodeRequest request) {
        Response<User> userResponse = userDomainService.loginByCode(
                request.getPhone(),
                request.getCode());
        return buildLoginResponse(userResponse);
    }
    
    /**
     * 构建登录响应的通用方法
     * @param userResponse 用户领域服务响应
     * @return 登录响应
     */
    private Response<LoginDto> buildLoginResponse(Response<User> userResponse) {
        // 如果登录失败，直接返回错误响应
        if (!"200".equals(userResponse.getCode())) {
            return Response.<LoginDto>builder()
                    .code(userResponse.getCode())
                    .info(userResponse.getInfo())
                    .build();
        }
        
        // 登录成功，生成 JWT token
        User user = userResponse.getData();
        // 构建响应
        String token = jwtUtil.generateToken(user.getUserId());
        LoginDto loginDto = new LoginDto();
        loginDto.setToken(token); // 设置生成的 token
        loginDto.setUserInfo(convertToResponse(user));

        return Response.<LoginDto>builder()
                .code("200")
                .info("登录成功")
                .data(loginDto)
                .build();
    }
    /**
     * 设置用户密码
     * @param request 设置密码请求
     * @return 设置成功响应
     */
    public Response<String> setPassword(SetPasswordRequest request) {
        Response<User> userResponse = userDomainService.setPassword(
                request.getUserId(),
                request.getPassword()
        );

        return buildSimpleResponse(userResponse);
    }
    
    /**
     * 构建简单响应的通用方法
     * @param response 用户领域服务响应
     * @return 简单响应
     */
    private Response<String> buildSimpleResponse(Response<?> response) {
        if (!"200".equals(response.getCode())) {
            return Response.<String>builder()
                    .code(response.getCode())
                    .info(response.getInfo())
                    .build();
        }

        return Response.<String>builder()
                .code("200")
                .info("操作成功")
                .build();
    }
     /**
     * 将领域实体转换为响应DTO
     * @param user 领域实体
     * @return 响应DTO
     */
    private RegisterDto convertToResponse(User user) {
        if (user == null) {
            return null;
        }
        RegisterDto response = new RegisterDto();
        BeanUtils.copyProperties(user, response);
        return response;
    }
    

    public Response<UserInfo> getUserInfo(Long userId) {
        Response<User> response = userDomainService.getUserInfo(userId);
        return buildUserInfoResponse(response);
    }
    
    public Response<UserInfo> me(String token) {
        // 从 token 中解析用户信息，而不是从 UserContext 获取
        Long userId = jwtUtil.validateToken(token);
        if (userId == null) {
            return Response.<UserInfo>builder()
                    .code("401")
                    .info("Token无效或已过期")
                    .build();
        }
        
        // 根据用户ID获取用户信息
        Response<User> userResponse = userDomainService.getUserInfo(userId);
        return buildUserInfoResponse(userResponse);
    }
    
    /**
     * 构建用户信息响应的通用方法
     * @param response 用户领域服务响应
     * @return 用户信息响应
     */
    private Response<UserInfo> buildUserInfoResponse(Response<User> response) {
        if (!"200".equals(response.getCode())) {
            return Response.<UserInfo>builder()
                    .code(response.getCode())
                    .info(response.getInfo())
                    .build();
        }
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(response.getData(), userInfo);
        return Response.<UserInfo>builder()
                .code(response.getCode())
                .info(response.getInfo())
                .data(userInfo)
                .build();
    }

    /**
     * 更新用户信息的新方法，通过token获取用户信息
     * @param userId 用户ID
     * @param updateUserInfoRequest 更新用户信息请求参数
     * @param token 用户认证token
     * @return 更新后的用户信息
     */
    public Response<UserInfo> updateUserInfoWithToken(Long userId, UpdateUserInfoRequest updateUserInfoRequest, String token) {
        // 权限验证：检查当前登录用户是否有权限更新目标用户信息
        if (token == null || token.isEmpty()) {
            return Response.<UserInfo>builder()
                    .code("401")
                    .info("用户未登录")
                    .build();
        }
        
        // 从token中解析用户ID
        Long currentUserId = jwtUtil.validateToken(token);
        if (currentUserId == null) {
            return Response.<UserInfo>builder()
                    .code("401")
                    .info("Token无效或已过期")
                    .build();
        }
        
        // 只有用户本人可以更新自己的信息
        if (!currentUserId.equals(userId)) {
            return Response.<UserInfo>builder()
                    .code("403")
                    .info("无权限更新其他用户信息")
                    .build();
        }
        
        Response<User> response = userDomainService.updateUserInfo(userId, updateUserInfoRequest);
        if (!"200".equals(response.getCode())) {
            return Response.<UserInfo>builder()
                    .code(response.getCode())
                    .info(response.getInfo())
                    .build();
        }
        
        User updatedUser = response.getData();
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(updatedUser, userInfo);
        
        return Response.<UserInfo>builder()
                .code(response.getCode())
                .info(response.getInfo())
                .data(userInfo)
                .build();
    }

    public Response<String> setPhone(Long userId, String phone) {
        return userDomainService.setPhone(userId, phone);
    }


}