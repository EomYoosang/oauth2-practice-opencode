package com.eomyoosang.oauth2.support.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PasswordPolicyValidatorTests {

    @Autowired
    private PasswordPolicyValidator validator;

    @Test
    void shouldPassWhenAllConditionsMet() {
        PasswordValidationResult result = validator.validate("StrongPass123!");

        assertThat(result.valid()).isTrue();
        assertThat(result.violations()).isEmpty();
    }

    @Test
    void shouldCollectViolations() {
        PasswordValidationResult result = validator.validate("weak");

        assertThat(result.valid()).isFalse();
        assertThat(result.violations()).isNotEmpty();
    }

    @Test
    void shouldThrowInvalidPasswordExceptionWhenUsingValidator() {
        assertThatThrownBy(() -> validator.validateOrThrow("weak"))
                .isInstanceOf(InvalidPasswordException.class);
    }
}
