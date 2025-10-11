package com.eomyoosang.oauth2.config.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 서명에 사용할 비밀 키. 운영 환경에서는 환경변수로 주입한다.
     */
    private String secret;

    /**
     * JWT 발급자(iss) 필드 값.
     */
    private String issuer;

    private final AccessToken accessToken = new AccessToken();

    private final RefreshToken refreshToken = new RefreshToken();

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public static class AccessToken {

        private long ttlSeconds;

        public long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }
    }

    public static class RefreshToken {

        private long ttlSeconds;

        /**
         * Redis 키 prefix. 예: auth:rt:
         */
        private String keyPrefix;

        /**
         * 사용자별 디바이스 registry prefix. 예: auth:dev:
         */
        private String deviceRegistryPrefix;

        public long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public String getDeviceRegistryPrefix() {
            return deviceRegistryPrefix;
        }

        public void setDeviceRegistryPrefix(String deviceRegistryPrefix) {
            this.deviceRegistryPrefix = deviceRegistryPrefix;
        }
    }
}
