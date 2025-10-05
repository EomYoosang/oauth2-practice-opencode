package com.eomyoosang.oauth2.support.security;

import com.eomyoosang.oauth2.config.security.PasswordProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NoOpCompromisedPasswordChecker implements CompromisedPasswordChecker {

    private static final Logger log = LoggerFactory.getLogger(NoOpCompromisedPasswordChecker.class);

    private final PasswordProperties.Compromised properties;

    public NoOpCompromisedPasswordChecker(PasswordProperties properties) {
        this.properties = properties.getCompromised();
    }

    @Override
    public boolean isCompromised(String rawPassword) {
        if (properties.isEnabled()) {
            log.warn("Compromised password checking is enabled but no provider is configured; treating as safe.");
        }
        return false;
    }

    @Override
    public String provider() {
        return properties.getProvider();
    }
}
