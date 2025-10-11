package com.eomyoosang.oauth2.auth.application;

import com.eomyoosang.oauth2.auth.application.command.EmailLoginCommand;
import com.eomyoosang.oauth2.auth.application.result.EmailLoginResult;
import com.eomyoosang.oauth2.auth.exception.EmailNotVerifiedException;
import com.eomyoosang.oauth2.auth.exception.InvalidCredentialsException;
import com.eomyoosang.oauth2.token.application.IssuedToken;
import com.eomyoosang.oauth2.token.application.JwtTokenProvider;
import com.eomyoosang.oauth2.token.application.RefreshTokenStore;
import com.eomyoosang.oauth2.support.security.PasswordHasher;
import com.eomyoosang.oauth2.user.domain.EmailAccount;
import com.eomyoosang.oauth2.user.domain.User;
import com.eomyoosang.oauth2.user.domain.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailLoginService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    @Transactional
    public EmailLoginResult login(EmailLoginCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        User user = userRepository.findByEmailAccount_Email(command.email())
                .orElseThrow(InvalidCredentialsException::new);

        EmailAccount emailAccount = user.getEmailAccount();
        if (emailAccount == null) {
            throw new InvalidCredentialsException();
        }

        if (!emailAccount.isVerified()) {
            throw new EmailNotVerifiedException();
        }

        if (!passwordHasher.matches(command.password(), emailAccount.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (passwordHasher.needsRehash(emailAccount.getPasswordHash())) {
            String rehashed = passwordHasher.hash(command.password());
            emailAccount.updatePasswordHash(rehashed);
        }

        emailAccount.recordSuccessfulLogin();
        user.recordSuccessfulLogin();

        String deviceId = resolveDeviceId(command.deviceId());
        UUID userId = user.getId();
        if (userId == null) {
            throw new IllegalStateException("User must be persisted before login");
        }

        IssuedToken accessToken = jwtTokenProvider.createAccessToken(userId, deviceId);
        IssuedToken refreshToken = jwtTokenProvider.createRefreshToken(userId, deviceId);

        refreshTokenStore.store(userId, deviceId, refreshToken.value());

        Instant now = clock.instant();
        long accessExpiresIn = accessToken.remainingSeconds(now);
        long refreshExpiresIn = refreshToken.remainingSeconds(now);

        return new EmailLoginResult(
                accessToken.value(),
                accessExpiresIn,
                refreshToken.value(),
                refreshExpiresIn,
                deviceId
        );
    }

    private String resolveDeviceId(String rawDeviceId) {
        if (rawDeviceId == null || rawDeviceId.isBlank()) {
            return refreshTokenStore.generateDeviceId();
        }
        return rawDeviceId.trim();
    }
}
