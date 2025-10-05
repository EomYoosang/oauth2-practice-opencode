package com.eomyoosang.oauth2.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.password")
public class PasswordProperties {

    /**
     * BCrypt 해싱 강도(cost). 기본값은 12.
     */
    private int bcryptStrength = 12;

    public int getBcryptStrength() {
        return bcryptStrength;
    }

    public void setBcryptStrength(int bcryptStrength) {
        this.bcryptStrength = bcryptStrength;
    }
}
