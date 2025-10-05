package com.eomyoosang.oauth2.auth.dto;

public record EmailSignupResponse(int statusCode, String message) {

    public static EmailSignupResponse success() {
        return new EmailSignupResponse(201, "회원가입 성공, 인증 메일을 확인하세요.");
    }
}
