package com.eomyoosang.oauth2.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.eomyoosang.oauth2.support.security.InvalidPasswordException;
import com.eomyoosang.oauth2.support.security.PasswordHasher;
import com.eomyoosang.oauth2.user.domain.EmailAccount;
import com.eomyoosang.oauth2.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailAccountRegistrationServiceTests {

    @Autowired
    private EmailAccountRegistrationService registrationService;

    @Autowired
    private PasswordHasher passwordHasher;

    @Test
    void shouldRegisterEmailAccountWithEncodedPassword() {
        User user = User.builder()
                .displayName("테스트 사용자")
                .build();

        EmailAccount account = registrationService.register(user, "user@example.com", "StrongPass123!");

        assertThat(account.getEmail()).isEqualTo("user@example.com");
        assertThat(account.getPasswordHash())
                .isNotBlank()
                .isNotEqualTo("StrongPass123!");
        assertThat(passwordHasher.matches("StrongPass123!", account.getPasswordHash())).isTrue();
        assertThat(user.getEmailAccount()).isNotNull();
    }

    @Test
    void shouldRejectWeakPassword() {
        User user = User.builder()
                .displayName("테스트 사용자")
                .build();

        assertThatThrownBy(() ->
                registrationService.register(user, "user@example.com", "weakpass"))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("최소");
        assertThat(user.getEmailAccount()).isNull();
    }
}
