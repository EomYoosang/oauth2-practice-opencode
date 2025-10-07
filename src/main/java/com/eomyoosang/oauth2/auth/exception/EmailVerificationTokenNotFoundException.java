package com.eomyoosang.oauth2.auth.exception;

import com.eomyoosang.oauth2.common.exception.BusinessException;
import com.eomyoosang.oauth2.common.exception.ErrorCode;

public class EmailVerificationTokenNotFoundException extends BusinessException {

    public EmailVerificationTokenNotFoundException() {
        super(ErrorCode.EMAIL_VERIFICATION_TOKEN_NOT_FOUND, "유효하지 않거나 만료된 인증 토큰입니다.");
    }
}
