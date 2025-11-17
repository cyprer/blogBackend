package com.cypress.user.service;

import com.cypress.enums.VerificationResult;
import com.cypress.response.Response;
import com.cypress.user.model.entity.User;

public interface IUserDomainService {
    String sendCode(String phone);
    Response<User> register(String phone, String password);
    Response<User> login(String loginKey, String password);
    Response<User> updateUsername(Long userId, String newUsername);
    VerificationResult validCode(String phone, String code);
}