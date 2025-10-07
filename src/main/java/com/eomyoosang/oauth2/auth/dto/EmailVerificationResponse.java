package com.eomyoosang.oauth2.auth.dto;

public record EmailVerificationResponse(int statusCode, String message) {

    public static EmailVerificationResponse success() {
        return new EmailVerificationResponse(200, "이메일 인증이 완료되었습니다.");
    }
}
