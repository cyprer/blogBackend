package com.cypress.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum VerificationResult {
    SUCCESS("200", "验证码验证成功"),
    INVALID("400", "验证码错误"),
    EXPIRED("400", "验证码已过期");

    private String code;
    private String info;
}