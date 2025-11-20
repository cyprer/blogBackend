package com.cypress.api;

import com.cypress.app.user.UserAppService;
import com.cypress.dto.LoginDto;
import com.cypress.dto.RegisterDto;
import com.cypress.dto.UserInfo;
import com.cypress.request.LoginByPasswordRequest;
import com.cypress.request.RegisterRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserAppService userAppService;

    @InjectMocks
    private UserController userController;

    private String testPhone = "13800138000";
    private String testPassword = "123456";
    private String testCode = "123456";
    private Long testUserId = 1L;
    private String testToken = "test-jwt-token";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testMeInterface() throws Exception {
        // 1. 模拟注册成功
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setPhone(testPhone);
        registerRequest.setCode(testCode);

        RegisterDto registerDto = new RegisterDto();
        registerDto.setUserId(testUserId);
        registerDto.setPhone(testPhone);
        Response<RegisterDto> registerResponse = Response.<RegisterDto>builder()
                .code("200")
                .info("注册成功")
                .data(registerDto)
                .build();
        when(userAppService.register(any(RegisterRequest.class))).thenReturn(registerResponse);

        // 执行注册请求
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phone\":\"" + testPhone + "\",\"password\":\"" + testPassword + "\",\"code\":\"" + testCode + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.userId").value(testUserId));

        // 2. 模拟登录成功
        LoginByPasswordRequest loginByPasswordRequest = new LoginByPasswordRequest();
        loginByPasswordRequest.setLoginKey(testPhone);
        loginByPasswordRequest.setPassword(testPassword);

        LoginDto loginDto = new LoginDto();
        loginDto.setToken(testToken);
        loginDto.setUserInfo(registerDto);
        Response<LoginDto> loginResponse = Response.<LoginDto>builder()
                .code("200")
                .info("登录成功")
                .data(loginDto)
                .build();
        when(userAppService.loginByPassword(any(LoginByPasswordRequest.class))).thenReturn(loginResponse);

        // 执行登录请求
        mockMvc.perform(post("/api/user/login-by-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginKey\":\"" + testPhone + "\",\"password\":\"" + testPassword + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.token").value(testToken));

        // 3. 模拟获取当前用户信息成功
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(testUserId);
        userInfo.setUsername("user8000"); // 匹配默认用户名生成规则
        Response<UserInfo> meResponse = Response.<UserInfo>builder()
                .code("200")
                .info("获取用户信息成功")
                .data(userInfo)
                .build();
        when(userAppService.me()).thenReturn(meResponse);

        // 执行获取当前用户信息请求（携带登录成功的token）
        mockMvc.perform(get("/api/user/me")
                        .header("Authorization", testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.userId").value(testUserId));
    }
}

