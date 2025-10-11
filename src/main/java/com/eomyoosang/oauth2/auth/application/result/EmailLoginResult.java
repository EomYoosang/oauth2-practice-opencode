package com.eomyoosang.oauth2.auth.application.result;

public record EmailLoginResult(String accessToken,
                               long accessTokenExpiresIn,
                               String refreshToken,
                               long refreshTokenExpiresIn,
                               String deviceId) {
}
