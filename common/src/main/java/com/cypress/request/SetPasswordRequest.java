package com.cypress.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "设置密码请求")
public class SetPasswordRequest {
    @ApiModelProperty(value = "用户ID", required = true, example = "1")
    private Long userId;

    @ApiModelProperty(value = "密码", required = true, example = "123456")
    private String password;
}