package com.eomyoosang.oauth2.token.exception;

import com.eomyoosang.oauth2.common.exception.BusinessException;
import com.eomyoosang.oauth2.common.exception.ErrorCode;

public class InvalidRefreshTokenException extends BusinessException {

    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN, "리프레시 토큰이 유효하지 않습니다.");
    }

    public InvalidRefreshTokenException(Throwable cause) {
        super(ErrorCode.INVALID_REFRESH_TOKEN, "리프레시 토큰이 유효하지 않습니다.");
        initCause(cause);
    }
}
