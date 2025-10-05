package com.eomyoosang.oauth2.support.security;

import java.util.Collections;
import java.util.List;

public record PasswordValidationResult(boolean valid, List<String> violations) {

    public static PasswordValidationResult success() {
        return new PasswordValidationResult(true, Collections.emptyList());
    }

    public static PasswordValidationResult failure(List<String> violations) {
        return new PasswordValidationResult(false, List.copyOf(violations));
    }
}
