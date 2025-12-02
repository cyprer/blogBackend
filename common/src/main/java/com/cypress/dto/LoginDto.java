package com.cypress.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String token;
    private UserInfo userInfo;
}
