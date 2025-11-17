package com.cypress.api;

import com.cypress.app.user.UserAppService;
import com.cypress.app.user.request.LoginRequest;
import com.cypress.app.user.request.RegisterRequest;
import com.cypress.app.user.dto.LoginDto;
import com.cypress.app.user.dto.RegisterDto;
import com.cypress.response.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 用户控制器 - 接口层
 * 处理用户相关的HTTP请求
 */
@RestController
@RequestMapping("/api/users")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserAppService userAppService;

    /**
     * 获取验证码接口
     * @param phone 手机号
     * @return 验证码
     */
    @PostMapping("/send_code")
    public String sendCode(@RequestParam String phone) {
        return userAppService.sendCode(phone);
    }



    /**
     * 用户注册接口
     * @param request 注册请求参数
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "使用手机号注册新用户")
    public Response<RegisterDto> register(@RequestBody RegisterRequest request) {
        return userAppService.register(request);
    }

    /**
     * 用户登录接口
     * @param request 登录请求参数
     * @return 登录成功后的令牌和用户信息
     */
    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "使用手机号或用户名登录")
    public Response<LoginDto> login(@RequestBody LoginRequest request) {
        // 直接返回UserAppService处理的结果
        return userAppService.login(request);
    }
    
    /**
     * 更新用户名接口
     * @param userId 用户ID
     * @param username 新用户名
     * @return 更新结果
     */
    @PostMapping("/update_username")
    @ApiOperation(value = "更新用户名", notes = "更新用户的用户名")
    public Response<RegisterDto> updateUsername(@RequestParam Long userId, @RequestParam String username) {
        return userAppService.updateUsername(userId, username);
    }

}