package com.eomyoosang.oauth2.auth.application;

import com.eomyoosang.oauth2.auth.application.command.EmailSignupCommand;
import com.eomyoosang.oauth2.auth.application.result.EmailSignupResult;
import com.eomyoosang.oauth2.auth.exception.EmailAlreadyRegisteredException;
import com.eomyoosang.oauth2.user.application.EmailAccountRegistrationService;
import com.eomyoosang.oauth2.user.domain.User;
import com.eomyoosang.oauth2.user.domain.UserRepository;
import com.eomyoosang.oauth2.user.domain.UserStatus;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailSignupService {

    private final UserRepository userRepository;
    private final EmailAccountRegistrationService emailAccountRegistrationService;

    @Transactional
    public EmailSignupResult register(EmailSignupCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        if (userRepository.existsByEmailAccount_Email(command.email())) {
            throw new EmailAlreadyRegisteredException();
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
