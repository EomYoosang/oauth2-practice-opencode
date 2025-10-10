package com.eomyoosang.oauth2.auth.exception;

import com.eomyoosang.oauth2.common.exception.BusinessException;
import com.eomyoosang.oauth2.common.exception.ErrorCode;

public class EmailAlreadyVerifiedException extends BusinessException {

    public EmailAlreadyVerifiedException() {
        super(ErrorCode.EMAIL_ALREADY_VERIFIED, "이미 인증이 완료된 이메일입니다.");
    }
}
