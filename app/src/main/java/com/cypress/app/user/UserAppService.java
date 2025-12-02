package com.cypress.app.user;


import com.cypress.dto.UpdateUserInfoResponse;
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
        // 将Long类型的userId转换为String类型避免前端精度问题
        userInfo.setUserId(String.valueOf(user.getUserId()));
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
        String token = jwtUtil.generateToken(user.getUserId());
        
        // 构造登录响应
        LoginDto loginDto = new LoginDto();
        loginDto.setToken(token);
        
        // 构造用户信息
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        // 将Long类型的userId转换为String类型避免前端精度问题
        userInfo.setUserId(String.valueOf(user.getUserId()));
        loginDto.setUserInfo(userInfo);
        
        return Response.<LoginDto>builder()
                .code("200")
                .info("登录成功")
                .data(loginDto)
                .build();
    }

    /**
     * 获取用户信息
     * @param userIdStr 用户ID字符串
     * @return 用户信息
     */
    public Response<UserInfo> getUserInfo(String userIdStr) {
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            return Response.<UserInfo>builder()
                    .code("400")
                    .info("用户ID格式不正确")
                    .build();
        }
        Response<User> userResponse = userDomainService.getUserInfo(userId);
        if (!"200".equals(userResponse.getCode())) {
            return Response.<UserInfo>builder()
                    .code(userResponse.getCode())
                    .info(userResponse.getInfo())
                    .build();
        }
        
        User user = userResponse.getData();
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(user, userInfo);
        // 将Long类型的userId转换为String类型避免前端精度问题
        userInfo.setUserId(String.valueOf(user.getUserId()));
        
        return Response.<UserInfo>builder()
                .code("200")
                .info("获取用户信息成功")
                .data(userInfo)
                .build();
    }

    /**
     * 使用token更新用户信息
     * @param userIdStr 用户ID字符串
     * @param updateUserInfoRequest 更新用户信息请求
     * @param token 用户认证token
     * @return 更新后的用户信息
     */
    public Response<UpdateUserInfoResponse> updateUserInfoWithToken(String userIdStr, UpdateUserInfoRequest updateUserInfoRequest, String token) {
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            return Response.<UpdateUserInfoResponse>builder()
                    .code("400")
                    .info("用户ID格式不正确")
                    .build();
        }
        
        // 权限验证：检查当前登录用户是否有权限更新目标用户信息
        if (token == null || token.isEmpty()) {
            return Response.<UpdateUserInfoResponse>builder()
                    .code("401")
                    .info("用户未登录")
                    .build();
        }
        
        // 从token中解析用户ID
        Long currentUserId = jwtUtil.validateToken(token);
        if (currentUserId == null) {
            return Response.<UpdateUserInfoResponse>builder()
                    .code("401")
                    .info("Token无效或已过期")
                    .build();
        }
        
        // 只有用户本人可以更新自己的信息
        if (!currentUserId.equals(userId)) {
            return Response.<UpdateUserInfoResponse>builder()
                    .code("403")
                    .info("无权限更新其他用户信息")
                    .build();
        }
        
        Response<User> response = userDomainService.updateUserInfo(userId, updateUserInfoRequest);
        if (!"200".equals(response.getCode())) {
            return Response.<UpdateUserInfoResponse>builder()
                    .code(response.getCode())
                    .info(response.getInfo())
                    .build();
        }
        
        User user = response.getData();
        if (user == null) {
            return Response.<UpdateUserInfoResponse>builder()
                    .code("500")
                    .info("服务器内部错误：用户数据为空")
                    .build();
        }
        
        UpdateUserInfoResponse updateUserInfoResponse = new UpdateUserInfoResponse();
        BeanUtils.copyProperties(user, updateUserInfoResponse);
        // 将Long类型的userId转换为String类型避免前端精度问题
        updateUserInfoResponse.setUserId(String.valueOf(user.getUserId()));
        
        // 无论userId是否变化，都生成新的token以确保安全性
        String newToken = jwtUtil.generateToken(user.getUserId());
        updateUserInfoResponse.setNewToken(newToken);
        
        return Response.<UpdateUserInfoResponse>builder()
                .code("200")
                .info("更新用户信息成功")
                .data(updateUserInfoResponse)
                .build();
    }

    /**
     * 获取当前登录用户信息
     * @param token 用户认证token
     * @return 当前登录用户信息
     */
    public Response<UserInfo> me(String token) {
        // 从token中解析用户ID
        Long userId = jwtUtil.validateToken(token);
        if (userId == null) {
            return Response.<UserInfo>builder()
                    .code("401")
                    .info("Token无效或已过期")
                    .build();
        }
        
        // 获取用户信息
        return getUserInfo(String.valueOf(userId));
    }

    /**
     * 设置用户密码
     * @param request 设置密码请求
     * @return 设置结果
     */
    public Response<String> setPassword(SetPasswordRequest request) {
        if (request.getUserId() == null) {
            return Response.<String>builder()
                    .code("400")
                    .info("用户ID不能为空")
                    .build();
        }
        Response<User> userResponse = userDomainService.setPassword(
                request.getUserId(),
                request.getPassword()
        );
        
        if (!"200".equals(userResponse.getCode())) {
            return Response.<String>builder()
                    .code(userResponse.getCode())
                    .info(userResponse.getInfo())
                    .build();
        }
        
        return Response.<String>builder()
                .code("200")
                .info("设置密码成功")
                .build();
    }

    /**
     * 设置用户手机号
     * @param userIdStr 用户ID字符串
     * @param phone 手机号
     * @return 设置结果
     */
    public Response<String> setPhone(String userIdStr, String phone) {
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            return Response.<String>builder()
                    .code("400")
                    .info("用户ID格式不正确")
                    .build();
        }
        Response<String> userResponse = userDomainService.setPhone(
                userId,
                phone
        );

        if (!"200".equals(userResponse.getCode())) {
            return Response.<String>builder()
                    .code(userResponse.getCode())
                    .info(userResponse.getInfo())
                    .build();
        }

        return Response.<String>builder()
                .code("200")
                .info("设置手机号成功")
                .data(userResponse.getData())
                .build();
    }

    /**
     * 将User转换为RegisterDto
     * @param user 用户实体
     * @return 注册响应DTO
     */
    private RegisterDto convertToResponse(User user) {
        RegisterDto registerDto = new RegisterDto();
        BeanUtils.copyProperties(user, registerDto);
        // 将Long类型的userId转换为String类型避免前端精度问题
        registerDto.setUserId(String.valueOf(user.getUserId()));
        return registerDto;
    }
}