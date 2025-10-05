package com.eomyoosang.oauth2.support.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BCryptPasswordHasherTests {

    @Autowired
    private PasswordHasher passwordHasher;

    @Test
    void shouldHashAndMatchPassword() {
        String rawPassword = "Passw0rd!";

        String encoded = passwordHasher.hash(rawPassword);

        assertThat(encoded)
                .isNotBlank()
                .isNotEqualTo(rawPassword);
        assertThat(passwordHasher.matches(rawPassword, encoded)).isTrue();
        assertThat(passwordHasher.needsRehash(encoded)).isFalse();
    }
}
