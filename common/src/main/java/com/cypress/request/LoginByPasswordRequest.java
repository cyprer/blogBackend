package com.cypress.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "使用密码登录请求")
public class LoginByPasswordRequest {
    @ApiModelProperty(value = "登录凭证（手机号或用户名）", required = true, example = "13800138000或张三")
    private String loginKey;

    @ApiModelProperty(value = "密码", required = true, example = "123456")
    private String password;
}
