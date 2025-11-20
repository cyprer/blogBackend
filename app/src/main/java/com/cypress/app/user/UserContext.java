package com.cypress.app.user;

import com.cypress.dto.UserInfo;


public class UserContext {

    private static final ThreadLocal<UserInfo> USER_CONTEXT = new ThreadLocal<>();

    public static long getThreadId() {
        return Thread.currentThread().getId();
    }

    public static void setUserInfo(UserInfo userInfo) {
        USER_CONTEXT.set(userInfo);
    }

    public static UserInfo getUserInfo() {
        return USER_CONTEXT.get();
    }

    public static void clear() {
        USER_CONTEXT.remove();
    }
}
