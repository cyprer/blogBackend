package com.cypress.app.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "用户登录请求")
public class LoginRequest {
    @ApiModelProperty(value = "登录凭证（手机号或用户名）", required = true, example = "13800138000或张三")
    private String loginKey;

    @ApiModelProperty(value = "密码", required = true, example = "123456")
    private String password;
}
