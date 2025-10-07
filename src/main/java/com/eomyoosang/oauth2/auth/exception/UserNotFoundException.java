package com.eomyoosang.oauth2.auth.exception;

import com.eomyoosang.oauth2.common.exception.BusinessException;
import com.eomyoosang.oauth2.common.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다.");
    }
}
