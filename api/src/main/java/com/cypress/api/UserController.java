package com.cypress.api;

import com.cypress.app.user.UserAppService;
import com.cypress.dto.UserInfo;
import com.cypress.request.*;
import com.cypress.dto.LoginDto;
import com.cypress.dto.RegisterDto;
import com.cypress.dto.UpdateUserInfoResponse;
import com.cypress.response.Response;
import com.cypress.utils.ResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户控制器 - 接口层
 * 处理用户相关的HTTP请求
 */
@RestController
@RequestMapping("/api/user")
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
    public ResponseEntity<Response<String>> sendCode(@RequestParam String phone) {
        Response<String> response = userAppService.sendCode(phone);
        return ResponseUtil.toResponseEntity(response);
    }



    /**
     * 用户注册接口
     * @param request 注册请求参数
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    @ApiOperation(value = "用户注册", notes = "使用手机号注册新用户")
    public ResponseEntity<Response<RegisterDto>> register(@RequestBody RegisterRequest request) {
        Response<RegisterDto> response = userAppService.register(request);
        return ResponseUtil.toResponseEntity(response);
    }

    /**
     * 用户登录接口
     * @param request 登录请求参数
     * @return 登录成功后的令牌和用户信息
     */
    @PostMapping("/login-by-password")
    @ApiOperation(value = "密码登录", notes = "使用手机号或用户名登录")
    public ResponseEntity<Response<LoginDto>> login(@RequestBody LoginByPasswordRequest request) {
        Response<LoginDto> response = userAppService.loginByPassword(request);
        return ResponseUtil.toResponseEntity(response);
    }

    @PostMapping("/login-by-code")
    @ApiOperation(value = "手机验证码登录", notes = "使用手机号和验证码登录")
    public ResponseEntity<Response<LoginDto>> loginByCode(@RequestBody LoginByCodeRequest request) {
        Response<LoginDto> response = userAppService.loginByCode(request);
        return ResponseUtil.toResponseEntity(response);
    }
    /**
     * 用户个人信息接口
     * @return 用户信息
     */
    @GetMapping("/me")
    @ApiOperation(value = "获取当前登录用户信息", notes = "获取当前登录用户的信息")
    public ResponseEntity<Response<UserInfo>> me() {
        Response<UserInfo> response = userAppService.me();
        return ResponseUtil.toResponseEntity(response);
    }

    /**
     * 设置用户密码接口
     * @param request 设置密码请求参数
     * @return 设置成功响应
     */
    @PostMapping("/set_password")
    @ApiOperation(value = "设置用户密码", notes = "注册成功后设置用户密码")
    public ResponseEntity<Response<String>> setPassword(@RequestBody SetPasswordRequest request) {
        Response<String> response = userAppService.setPassword(request);
        return ResponseUtil.toResponseEntity(response);
    }

    /**
     * 设置用户手机号接口
     * @param userId 用户ID
     * @param phone 新手机号
     */
    @PostMapping("/{userId}/phone")
    @ApiOperation(value = "设置用户手机号", notes = "设置用户手机号")
    public ResponseEntity<Response<String>> setPhone(@PathVariable Long userId, @RequestParam String phone) {
        Response<String> response = userAppService.setPhone(userId, phone);
        return ResponseUtil.toResponseEntity(response);
    }

    /**
     * 获取用户信息接口
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    @ApiOperation(value = "获取用户信息", notes = "根据用户ID获取用户信息")
    public ResponseEntity<Response<UserInfo>> getUserInfo(@PathVariable Long userId) {
        Response<UserInfo> response = userAppService.getUserInfo(userId);
        return ResponseUtil.toResponseEntity(response);
    }

    /**
     * 更新用户个人信息接口
     * @param userId 用户ID
     * @param updateUserInfoRequest 更新用户信息请求参数
     * @return 更新成功后的用户信息
     */
    @PatchMapping("/{userId}")
    @ApiOperation(value = "更新用户公开信息", notes = "使用PATCH方法部分更新用户信息，只传需要更新的字段")
    public ResponseEntity<Response<UserInfo>> updateUserInfo(@PathVariable Long userId, @RequestBody @Valid UpdateUserInfoRequest updateUserInfoRequest) {
        Response<UserInfo> response = userAppService.updateUserInfo(userId, updateUserInfoRequest);
        return ResponseUtil.toResponseEntity(response);
    }

}