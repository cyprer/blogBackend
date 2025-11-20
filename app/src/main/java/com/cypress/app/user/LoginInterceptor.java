package com.cypress.app.user;

import com.cypress.dto.UserInfo;
import com.cypress.response.Response;
import com.cypress.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserAppService userAppService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取token
        String token = request.getHeader("Authorization");

        // 如果没有token，返回未授权错误
        if (token == null || token.isEmpty()) {
            responseUnauthorized(response, "未提供认证令牌");
            return false;
        }

        // 验证token有效性
        Long userId = jwtUtil.validateToken(token);
        if (userId == null) {
            responseUnauthorized(response, "令牌无效或已过期");
            return false;
        }

        // 获取用户信息并设置到上下文
        Response<UserInfo> userInfoResponse = userAppService.getUserInfo(userId);
        if (!"200".equals(userInfoResponse.getCode()) || userInfoResponse.getData() == null) {
            responseUnauthorized(response, "用户信息不存在");
            return false;
        }

        // 将用户信息保存到线程上下文
        UserContext.setUserInfo(userInfoResponse.getData());

        // 验证通过，继续执行后续操作
        return true;
    }

    private void responseUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("{\"code\":\"401\",\"info\":\"" + message + "\"}");
        writer.flush();
    }
}
