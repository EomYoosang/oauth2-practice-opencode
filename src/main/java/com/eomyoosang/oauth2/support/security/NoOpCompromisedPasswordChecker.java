package com.eomyoosang.oauth2.support.security;

import com.eomyoosang.oauth2.config.security.PasswordProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoOpCompromisedPasswordChecker implements CompromisedPasswordChecker {

    private static final Logger log = LoggerFactory.getLogger(NoOpCompromisedPasswordChecker.class);

    private final PasswordProperties passwordProperties;

    @Override
    public boolean isCompromised(String rawPassword) {
        PasswordProperties.Compromised compromised = passwordProperties.getCompromised();
        if (compromised.isEnabled()) {
            log.warn("Compromised password checking is enabled but no provider is configured; treating as safe.");
        }
        return false;
    }

    @Override
    public String provider() {
        return passwordProperties.getCompromised().getProvider();
    }
}
