package com.cypress.user.service;

import com.cypress.enums.VerificationResult;
import com.cypress.request.UpdateUserInfoRequest;
import com.cypress.response.Response;
import com.cypress.user.model.entity.User;

public interface IUserDomainService {
    String sendCode(String phone);
    Response<User> register(String phone, String code);
    Response<User> loginByPassword(String loginKey, String password);
    Response<User> loginByCode(String loginKey, String code);
    VerificationResult validCode(String phone, String code);
    Response<User> getUserInfo(Long userId);
    Response<User> updateUserInfo(Long userId, UpdateUserInfoRequest UserInfoRequest);
    Response<User> setPassword(Long userId, String password);
    Response<String> setPhone(Long userId, String phone);

}