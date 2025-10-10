package com.eomyoosang.oauth2.auth.application;

import com.eomyoosang.oauth2.auth.application.payload.EmailVerificationPayload;
import com.eomyoosang.oauth2.auth.exception.EmailAlreadyVerifiedException;
import com.eomyoosang.oauth2.auth.exception.EmailVerificationTokenNotFoundException;
import com.eomyoosang.oauth2.auth.exception.UserNotFoundException;
import com.eomyoosang.oauth2.user.domain.EmailAccount;
import com.eomyoosang.oauth2.user.domain.User;
import com.eomyoosang.oauth2.user.domain.UserRepository;
import com.eomyoosang.oauth2.user.domain.UserStatus;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenService tokenService;
    private final UserRepository userRepository;

    public EmailVerificationService(EmailVerificationTokenService tokenService,
                                    UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Transactional
    public String issueToken(User user) {
        if (user == null || user.getEmailAccount() == null) {
            throw new UserNotFoundException();
        }
        UUID userId = user.getId();
        if (userId == null) {
            throw new IllegalStateException("User must be persisted before issuing verification token");
        }
        return tokenService.createToken(userId, user.getEmailAccount().getEmail());
    }

    @Transactional
    public void verify(String token) {
        EmailVerificationPayload payload = tokenService.consumeToken(token)
                .orElseThrow(EmailVerificationTokenNotFoundException::new);

        User user = userRepository.findById(payload.userId())
                .orElseThrow(UserNotFoundException::new);

        EmailAccount emailAccount = user.getEmailAccount();
        if (emailAccount == null) {
            throw new UserNotFoundException();
        }

        if (!payload.email().equals(emailAccount.getEmail())) {
            throw new EmailVerificationTokenNotFoundException();
        }

        if (emailAccount.isVerified()) {
            throw new EmailAlreadyVerifiedException();
        }

        emailAccount.markVerified();
        if (user.getStatus() == UserStatus.PENDING) {
            user.activate();
        }
    }
}
