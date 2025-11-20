package com.cypress.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "更新用户信息响应")
public class UpdateUserInfoResponse {

    @ApiModelProperty(value = "用户信息")
    private UserInfo userInfo;

    @ApiModelProperty(value = "新的访问令牌（当userId发生变化时返回）")
    private String newToken;

    @ApiModelProperty(value = "是否userId发生了变化")
    private Boolean userIdChanged;
}