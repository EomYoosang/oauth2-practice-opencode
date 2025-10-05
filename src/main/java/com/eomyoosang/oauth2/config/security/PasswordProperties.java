package com.eomyoosang.oauth2.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.password")
public class PasswordProperties {

    /**
     * BCrypt 해싱 강도(cost). 기본값은 12.
     */
    private int bcryptStrength = 12;

    private final Policy policy = new Policy();

    private final Compromised compromised = new Compromised();

    public int getBcryptStrength() {
        return bcryptStrength;
    }

    public void setBcryptStrength(int bcryptStrength) {
        this.bcryptStrength = bcryptStrength;
    }

    public Policy getPolicy() {
        return policy;
    }

    public Compromised getCompromised() {
        return compromised;
    }

    public static class Policy {

        private int minimumLength = 12;

        private boolean requireUppercase = true;

        private boolean requireLowercase = true;

        private boolean requireDigit = true;

        private boolean requireSpecial = true;

        private String specialCharacters = "!@#$%^&*()_+-=[]{}|;:'\",.<>/?";

        public int getMinimumLength() {
            return minimumLength;
        }

        public void setMinimumLength(int minimumLength) {
            this.minimumLength = minimumLength;
        }

        public boolean isRequireUppercase() {
            return requireUppercase;
        }

        public void setRequireUppercase(boolean requireUppercase) {
            this.requireUppercase = requireUppercase;
        }

        public boolean isRequireLowercase() {
            return requireLowercase;
        }

        public void setRequireLowercase(boolean requireLowercase) {
            this.requireLowercase = requireLowercase;
        }

        public boolean isRequireDigit() {
            return requireDigit;
        }

        public void setRequireDigit(boolean requireDigit) {
            this.requireDigit = requireDigit;
        }

        public boolean isRequireSpecial() {
            return requireSpecial;
        }

        public void setRequireSpecial(boolean requireSpecial) {
            this.requireSpecial = requireSpecial;
        }

        public String getSpecialCharacters() {
            return specialCharacters;
        }

        public void setSpecialCharacters(String specialCharacters) {
            this.specialCharacters = specialCharacters;
        }
    }

    public static class Compromised {

        private boolean enabled = false;

        private String provider = "none";

        private String apiUrl;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }
    }
}
