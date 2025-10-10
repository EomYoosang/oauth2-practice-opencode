package com.eomyoosang.oauth2.auth.dto;

public record EmailSignupResponse(int statusCode, String message, String verificationToken) {

    public static EmailSignupResponse success(String verificationToken) {
        return new EmailSignupResponse(201, "회원가입 성공, 인증 메일을 확인하세요.", verificationToken);
    }
}
