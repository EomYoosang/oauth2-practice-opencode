package com.eomyoosang.oauth2.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    VALIDATION_FAILED("A1000", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    PASSWORD_POLICY_VIOLATION("A1001", HttpStatus.BAD_REQUEST, "비밀번호 정책을 만족하지 않습니다."),
    USER_EMAIL_DUPLICATED("U1000", HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
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
