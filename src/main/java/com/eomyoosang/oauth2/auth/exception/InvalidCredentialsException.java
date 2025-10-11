package com.eomyoosang.oauth2.auth.exception;

import com.eomyoosang.oauth2.common.exception.BusinessException;
import com.eomyoosang.oauth2.common.exception.ErrorCode;

public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException() {
        super(ErrorCode.INVALID_CREDENTIALS, "이메일 또는 비밀번호가 올바르지 않습니다.");
    }
}
