package com.eomyoosang.oauth2.user.application;

import com.eomyoosang.oauth2.support.security.PasswordHasher;
import com.eomyoosang.oauth2.support.security.PasswordPolicyValidator;
import com.eomyoosang.oauth2.user.domain.EmailAccount;
import com.eomyoosang.oauth2.user.domain.User;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class EmailAccountRegistrationService {

    private final PasswordPolicyValidator passwordPolicyValidator;
    private final PasswordHasher passwordHasher;

    public EmailAccountRegistrationService(PasswordPolicyValidator passwordPolicyValidator,
                                           PasswordHasher passwordHasher) {
        this.passwordPolicyValidator = passwordPolicyValidator;
        this.passwordHasher = passwordHasher;
    }

    public EmailAccount register(User user, String email, String rawPassword) {
        Objects.requireNonNull(user, "user must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");

        passwordPolicyValidator.validateOrThrow(rawPassword);
        String hashedPassword = passwordHasher.hash(rawPassword);
        EmailAccount account = EmailAccount.builder()
                .email(email)
                .passwordHash(hashedPassword)
                .verified(false)
                .build();

        user.registerEmailAccount(account);
        return account;
    }
}
