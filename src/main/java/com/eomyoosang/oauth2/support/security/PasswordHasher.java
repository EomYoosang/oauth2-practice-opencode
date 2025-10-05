package com.eomyoosang.oauth2.support.security;

public interface PasswordHasher {

    String hash(CharSequence rawPassword);

    boolean matches(CharSequence rawPassword, String encodedPassword);

    boolean needsRehash(String encodedPassword);
}
