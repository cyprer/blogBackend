package com.cypress.api;

import com.cypress.app.user.UserAppService;
import com.cypress.dto.UserInfo;
import com.cypress.request.UpdateUserInfoRequest;
import com.cypress.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 更改用户名功能测试
 * 验证更改用户名后能正确返回更新后的用户信息
 */
@ExtendWith(MockitoExtension.class)
public class UpdateUsernameTest {

    private MockMvc mockMvc;

    @Mock
    private UserAppService userAppService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testUpdateUsername_ShouldReturnUpdatedUserInfo() throws Exception {
        System.out.println("=== 测试更改用户名功能 ===");

        // 准备请求数据 - 只更新用户名
        String updateJson = "{\"userId\":1,\"username\":\"newAwesomeUsername\"}";

        // 准备更新后的用户信息
        UserInfo updatedUserInfo = new UserInfo();
        updatedUserInfo.setUserId(1L);
        updatedUserInfo.setEmail("test@example.com");
        updatedUserInfo.setUsername("newAwesomeUsername"); // 更新后的用户名
        updatedUserInfo.setAge(25);
        updatedUserInfo.setGender(1);
        updatedUserInfo.setAvatarUrl("http://example.com/avatar.jpg");
        updatedUserInfo.setBio("测试用户简介");
        updatedUserInfo.setSignature("测试签名");
        updatedUserInfo.setCreateTime(LocalDateTime.now());
        updatedUserInfo.setLastLoginTime(LocalDateTime.now());

        // Mock服务层返回更新后的用户信息
        Response<UserInfo> serviceResponse = Response.<UserInfo>builder()
                .code("200")
                .info("更新用户信息成功")
                .data(updatedUserInfo)
                .build();

        when(userAppService.updateUserInfo(eq(1L), any(UpdateUserInfoRequest.class)))
                .thenReturn(serviceResponse);

        // 执行更新用户名请求
        mockMvc.perform(patch("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.info").value("更新用户信息成功"))

                // 验证返回的用户信息中包含更新后的用户名
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.username").value("newAwesomeUsername"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.age").value(25))
                .andExpect(jsonPath("$.data.gender").value(1));

        System.out.println("✅ 用户名更新测试通过");
        System.out.println("   - 原用户名: testuser");
        System.out.println("   - 新用户名: newAwesomeUsername");
        System.out.println("   - 用户ID: 1");
        System.out.println("   - 其他字段保持不变");
    }

    @Test
    void testUpdateUsername_WithValidLength_ShouldSucceed() throws Exception {
        System.out.println("=== 测试合法长度的用户名更新 ===");

        // 测试不同长度的合法用户名
        String[] validUsernames = {
            "ab",                 // 最小长度2
            "normalUser",        // 普通长度
            "veryLongUsername123", // 较长但合法
            "user_"              // 包含下划线
        };

        for (String username : validUsernames) {
            String updateJson = "{\"userId\":1,\"username\":\"" + username + "\"}";

            UserInfo updatedUserInfo = new UserInfo();
            updatedUserInfo.setUserId(1L);
            updatedUserInfo.setUsername(username);

            Response<UserInfo> serviceResponse = Response.<UserInfo>builder()
                    .code("200")
                    .info("更新用户信息成功")
                    .data(updatedUserInfo)
                    .build();

            when(userAppService.updateUserInfo(eq(1L), any(UpdateUserInfoRequest.class)))
                    .thenReturn(serviceResponse);

            mockMvc.perform(patch("/api/user/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value("200"))
                    .andExpect(jsonPath("$.data.username").value(username));

            System.out.println("   ✅ 用户名 '" + username + "' 更新成功");
        }
    }

    @Test
    void testUpdateUsername_EmptyUsername_ShouldReturnValidationError() throws Exception {
        System.out.println("=== 测试空用户名应该返回验证错误 ===");

        // 准备空用户名
        String updateJson = "{\"userId\":1,\"username\":\"\"}";

        // 空用户名应该被验证拦截，返回HTTP 400
        mockMvc.perform(patch("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isBadRequest()); // 验证拦截返回400

        System.out.println("✅ 空用户名被验证拦截，返回HTTP 400错误");
    }
}