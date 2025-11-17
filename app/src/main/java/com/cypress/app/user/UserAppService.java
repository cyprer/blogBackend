package com.cypress.app.user;


import com.cypress.app.user.request.LoginRequest;
import com.cypress.app.user.request.RegisterRequest;
import com.cypress.app.user.dto.LoginDto;
import com.cypress.app.user.dto.RegisterDto;
import com.cypress.enums.VerificationResult;
import com.cypress.exception.AppException;
import com.cypress.user.model.entity.User;
import com.cypress.user.service.IUserDomainService;
import com.cypress.utils.JwtUtil;
import com.cypress.response.Response;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户应用服务 - 应用层
 * 作为控制器和领域服务之间的桥梁，负责协调领域对象完成业务操作
 */
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
    public String sendCode(String phone) {
        return userDomainService.sendCode(phone);
    }

    /**
     * 用户注册
     * @param request 注册请求
     * @return 用户响应
     */
    public Response<RegisterDto> register(RegisterRequest request) {
        // 调用领域服务进行注册
        VerificationResult result = userDomainService.validCode(request.getPhone(), request.getCode());
        if (result == VerificationResult.INVALID) {
            // 验证码错误
            return Response.<RegisterDto>builder()
                    .code(result.getCode())
                    .info(result.getInfo())
                    .build();
        } else if (result == VerificationResult.EXPIRED) {
            // 验证码过期
            return Response.<RegisterDto>builder()
                    .code(result.getCode())
                    .info(result.getInfo())
                    .build();
        }
        
        Response<User> userResponse = userDomainService.register(
                request.getPhone(),
                request.getPassword()
        );
        
        // 如果注册失败，直接返回错误响应
        if (!"200".equals(userResponse.getCode())) {
            return Response.<RegisterDto>builder()
                    .code(userResponse.getCode())
                    .info(userResponse.getInfo())
                    .build();
        }
        
        // 注册成功，转换为RegisterDto
        RegisterDto registerDto = convertToResponse(userResponse.getData());
        return Response.<RegisterDto>builder()
                .code("200")
                .info("注册成功")
                .data(registerDto)
                .build();
    }

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应
     */
    public Response<LoginDto> login(LoginRequest request) {
        // 调用领域服务进行登录
        Response<User> userResponse = userDomainService.login(
                request.getLoginKey(),
                request.getPassword()
        );
        
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

        // 构建响应
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
     * 更新用户名
     * @param userId 用户ID
     * @param username 新用户名
     * @return 用户响应
     */
    public Response<RegisterDto> updateUsername(Long userId, String username) {
        Response<User> userResponse = userDomainService.updateUsername(userId, username);
        
        // 如果更新失败，直接返回错误响应
        if (!"200".equals(userResponse.getCode())) {
            return Response.<RegisterDto>builder()
                    .code(userResponse.getCode())
                    .info(userResponse.getInfo())
                    .build();
        }
        
        // 更新成功，转换为RegisterDto
        RegisterDto registerDto = convertToResponse(userResponse.getData());
        return Response.<RegisterDto>builder()
                .code("200")
                .info("用户名更新成功")
                .data(registerDto)
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

}