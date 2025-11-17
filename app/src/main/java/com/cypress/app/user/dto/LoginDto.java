package com.cypress.app.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "登录响应")
public class LoginDto {
    @ApiModelProperty(value = "JWT令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @ApiModelProperty(value = "用户信息")
    private RegisterDto userInfo;
}
