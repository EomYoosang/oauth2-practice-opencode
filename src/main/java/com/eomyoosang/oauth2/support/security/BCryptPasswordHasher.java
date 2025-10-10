package com.eomyoosang.oauth2.support.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String hash(CharSequence rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @Override
    public boolean needsRehash(String encodedPassword) {
        return passwordEncoder.upgradeEncoding(encodedPassword);
    }
}
