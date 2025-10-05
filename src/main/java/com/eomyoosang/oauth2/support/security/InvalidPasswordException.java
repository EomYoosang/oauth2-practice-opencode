package com.eomyoosang.oauth2.support.security;

import java.util.List;

public class InvalidPasswordException extends RuntimeException {

    private final List<String> violations;

    public InvalidPasswordException(List<String> violations) {
        super(String.join(", ", violations));
        this.violations = List.copyOf(violations);
    }

    public List<String> getViolations() {
        return violations;
    }
}
