package com.eomyoosang.oauth2.auth.dto;

import com.eomyoosang.oauth2.auth.application.result.EmailLoginResult;

public record EmailLoginResponse(
        String tokenType,
        String accessToken,
        long accessTokenExpiresIn,
        String refreshToken,
        long refreshTokenExpiresIn,
        String deviceId
) {

    private static final String BEARER = "Bearer";

    public static EmailLoginResponse from(EmailLoginResult result) {
        return new EmailLoginResponse(
                BEARER,
                result.accessToken(),
                result.accessTokenExpiresIn(),
                result.refreshToken(),
                result.refreshTokenExpiresIn(),
                result.deviceId()
        );
    }
}
