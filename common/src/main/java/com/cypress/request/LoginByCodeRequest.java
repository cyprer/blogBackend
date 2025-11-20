package com.cypress.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "使用验证码登录请求")
public class LoginByCodeRequest {
    @ApiModelProperty(value = "手机号", required = true, example = "13800138000")
    private String phone;

    @ApiModelProperty(value = "验证码", required = true, example = "123456")
    private String code;
}
