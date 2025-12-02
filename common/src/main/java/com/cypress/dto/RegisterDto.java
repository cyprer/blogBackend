package com.cypress.dto;

import lombok.Data;

@Data
public class RegisterDto {
    /**
     * 用户ID - 唯一标识
     * 使用String类型避免JavaScript中Number精度问题
     */
    private String userId;
    
    private String phone;
    private String email;
    private String username;
    private Integer age;
    private Integer gender;
    private String avatarUrl;
    private String bio;
    private String signature;
}
