package com.eomyoosang.oauth2.config.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.email-verification")
public class EmailVerificationProperties {

    /**
     * Redis key prefix for email verification tokens.
     */
    private String keyPrefix = "auth:ev:";

    /**
     * Token time-to-live in seconds.
     */
    private long ttlSeconds = 900;

    /**
     * Number of random bytes used to build the verification token string.
     */
    private int tokenByteLength = 32;

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public long getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(long ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public int getTokenByteLength() {
        return tokenByteLength;
    }

    public void setTokenByteLength(int tokenByteLength) {
        this.tokenByteLength = tokenByteLength;
    }
}
