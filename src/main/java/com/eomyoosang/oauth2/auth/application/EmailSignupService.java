package com.eomyoosang.oauth2.auth.application;

import com.eomyoosang.oauth2.auth.application.command.EmailSignupCommand;
import com.eomyoosang.oauth2.auth.application.result.EmailSignupResult;
import com.eomyoosang.oauth2.auth.exception.EmailAlreadyRegisteredException;
import com.eomyoosang.oauth2.user.application.EmailAccountRegistrationService;
import com.eomyoosang.oauth2.user.domain.User;
import com.eomyoosang.oauth2.user.domain.UserRepository;
import com.eomyoosang.oauth2.user.domain.UserStatus;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailSignupService {

    private final UserRepository userRepository;
    private final EmailAccountRegistrationService emailAccountRegistrationService;

    public EmailSignupService(UserRepository userRepository,
                              EmailAccountRegistrationService emailAccountRegistrationService) {
        this.userRepository = userRepository;
        this.emailAccountRegistrationService = emailAccountRegistrationService;
    }

    @Transactional
    public EmailSignupResult register(EmailSignupCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyRegisteredException(command.email());
        }

        User user = User.builder()
                .displayName(command.name())
                .status(UserStatus.PENDING)
                .build();

        emailAccountRegistrationService.register(user, command.email(), command.password());
        User saved = userRepository.save(user);
        return new EmailSignupResult(saved);
    }
}
