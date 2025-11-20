package com.cypress.api;

import com.cypress.app.user.UserAppService;
import com.cypress.request.UpdateUserInfoRequest;
import com.cypress.response.Response;
import com.cypress.dto.UserInfo;
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
 * UserController 更新用户信息 API 简化测试
 * 注意：由于当前实现中UserController和UserAppService的类型不匹配，
 * 这个测试暂时无法正常运行，主要展示测试结构和逻辑
 */
@ExtendWith(MockitoExtension.class)
public class UserControllerUpdateSimpleTest {

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
    void testUpdateUserInfo_NormalFieldUpdate_ShouldReturnSuccess() throws Exception {
        System.out.println("API测试：普通字段更新（使用原始UserInfo响应）");

        // 准备请求数据（必须包含userId）
        UpdateUserInfoRequest request = new UpdateUserInfoRequest();
        request.setUserId(1L); // 必须提供userId
        request.setEmail("newemail@example.com");
        request.setUsername("newusername");
        request.setAge(26);

        // 准备响应数据（使用UserInfo而不是UpdateUserInfoResponse）
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(1L);
        userInfo.setEmail("newemail@example.com");
        userInfo.setUsername("newusername");
        userInfo.setAge(26);
        userInfo.setCreateTime(LocalDateTime.now());
        userInfo.setLastLoginTime(LocalDateTime.now());

        Response<UserInfo> serviceResponse = Response.<UserInfo>builder()
                .code("200")
                .info("更新用户信息成功")
                .data(userInfo)
                .build();

        // Mock服务层
        when(userAppService.updateUserInfo(eq(1L), any(UpdateUserInfoRequest.class)))
                .thenReturn(serviceResponse);

        // 执行请求
        mockMvc.perform(patch("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"email\":\"newemail@example.com\",\"username\":\"newusername\",\"age\":26}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.info").value("更新用户信息成功"))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.email").value("newemail@example.com"))
                .andExpect(jsonPath("$.data.username").value("newusername"))
                .andExpect(jsonPath("$.data.age").value(26));

        System.out.println("✅ API普通更新测试通过（使用UserInfo响应）");
    }

    @Test
    void testUpdateUserInfo_Unauthorized_ShouldReturn401() throws Exception {
        System.out.println("API测试：未授权访问");

        // 准备请求数据（必须包含userId）
        UpdateUserInfoRequest request = new UpdateUserInfoRequest();
        request.setUserId(1L); // 必须提供userId
        request.setEmail("test@example.com");

        // Mock服务层返回401
        Response<UserInfo> serviceResponse = Response.<UserInfo>builder()
                .code("401")
                .info("用户未登录")
                .build();

        when(userAppService.updateUserInfo(eq(1L), any(UpdateUserInfoRequest.class)))
                .thenReturn(serviceResponse);

        // 执行请求 - 由于拦截器会拦截未登录请求，我们测试拦截器的行为
        mockMvc.perform(patch("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"email\":\"test@example.com\"}"))
                .andExpect(status().isUnauthorized()); // 拦截器返回HTTP 401

        System.out.println("✅ API未授权测试通过（拦截器返回HTTP 401）");
    }

    @Test
    void testUpdateUserInfo_ValidationError_ShouldReturn400() throws Exception {
        System.out.println("API测试：参数验证错误");

        // 准备无效的请求数据（邮箱格式错误）
        String invalidRequest = "{\"email\":\"invalid-email\",\"username\":\"a\"}";

        // 执行请求 - 由于参数验证失败，Spring会直接返回400，不会调用service层
        mockMvc.perform(patch("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest()); // Spring Validation会返回400

        System.out.println("✅ API参数验证测试通过");
    }
}