package com.eomyoosang.oauth2.auth.exception;

import com.eomyoosang.oauth2.common.exception.BusinessException;
import com.eomyoosang.oauth2.common.exception.ErrorCode;

public class EmailAlreadyRegisteredException extends BusinessException {

    public EmailAlreadyRegisteredException(String email) {
        super(ErrorCode.USER_EMAIL_DUPLICATED, "이미 등록된 이메일입니다: %s".formatted(email));
    }
}
