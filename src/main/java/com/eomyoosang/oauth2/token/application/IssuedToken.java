package com.eomyoosang.oauth2.token.application;

import java.time.Instant;

public record IssuedToken(String value, Instant expiresAt, TokenType tokenType) {

    public long remainingSeconds(Instant now) {
        return Math.max(0, expiresAt.getEpochSecond() - now.getEpochSecond());
    }
}
