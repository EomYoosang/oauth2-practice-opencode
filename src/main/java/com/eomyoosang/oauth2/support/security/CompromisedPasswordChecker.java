package com.eomyoosang.oauth2.support.security;

public interface CompromisedPasswordChecker {

    boolean isCompromised(String rawPassword);

    String provider();
}
