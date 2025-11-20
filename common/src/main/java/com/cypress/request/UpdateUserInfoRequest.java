package com.cypress.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "更新用户信息请求")
public class UpdateUserInfoRequest {

    @ApiModelProperty(value = "用户ID（可选，不更新则不传此字段）")
    private Long userId;

    @ApiModelProperty(value = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @ApiModelProperty(value = "用户名")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20个字符之间")
    private String username;

    @ApiModelProperty(value = "年龄")
    @Min(value = 1, message = "年龄必须大于0")
    @Max(value = 150, message = "年龄不能超过150")
    private Integer age;

    @ApiModelProperty(value = "性别")
    @Min(value = 0, message = "性别值无效")
    @Max(value = 2, message = "性别值无效")
    private Integer gender;

    @ApiModelProperty(value = "头像URL")
    @URL(message = "头像URL格式不正确")
    private String avatarUrl;

    @ApiModelProperty(value = "个人简介")
    @Size(max = 200, message = "个人简介长度不能超过200个字符")
    private String bio;

    @ApiModelProperty(value = "个性签名")
    @Size(max = 100, message = "个性签名长度不能超过100个字符")
    private String signature;
}
