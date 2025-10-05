package com.eomyoosang.oauth2.common.response;

import com.eomyoosang.oauth2.common.exception.ErrorCode;
import java.time.Instant;

public record ApiErrorResponse(Instant timestamp, int statusCode, String errorCode, String message, String path) {

    public static ApiErrorResponse of(ErrorCode errorCode, String message, String path) {
        return new ApiErrorResponse(
                Instant.now(),
                errorCode.getStatus().value(),
                errorCode.getCode(),
                message != null ? message : errorCode.getDefaultMessage(),
                path
        );
    }
}
