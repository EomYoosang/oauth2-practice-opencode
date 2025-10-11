package com.eomyoosang.oauth2.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    VALIDATION_FAILED("A1000", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    PASSWORD_POLICY_VIOLATION("A1001", HttpStatus.BAD_REQUEST, "비밀번호 정책을 만족하지 않습니다."),
    EMAIL_VERIFICATION_TOKEN_NOT_FOUND("A1002", HttpStatus.NOT_FOUND, "이메일 인증 토큰을 찾을 수 없습니다."),
    EMAIL_ALREADY_VERIFIED("A1003", HttpStatus.CONFLICT, "이미 인증된 이메일입니다."),
    INVALID_CREDENTIALS("A1004", HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN("A1005", HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다."),
    USER_EMAIL_DUPLICATED("U1000", HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
    USER_NOT_FOUND("U1001", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EMAIL_NOT_VERIFIED("U1002", HttpStatus.FORBIDDEN, "이메일 인증이 필요합니다."),
    INTERNAL_SERVER_ERROR("S1000", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(String code, HttpStatus status, String defaultMessage) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
