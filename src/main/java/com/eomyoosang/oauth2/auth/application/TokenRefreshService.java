package com.eomyoosang.oauth2.auth.application;

import com.eomyoosang.oauth2.auth.application.result.EmailLoginResult;
import com.eomyoosang.oauth2.auth.exception.EmailNotVerifiedException;
import com.eomyoosang.oauth2.token.application.IssuedToken;
import com.eomyoosang.oauth2.token.application.JwtTokenProvider;
import com.eomyoosang.oauth2.token.application.JwtTokenProvider.ParsedJwt;
import com.eomyoosang.oauth2.token.application.RefreshTokenStore;
import com.eomyoosang.oauth2.token.application.TokenType;
import com.eomyoosang.oauth2.token.exception.InvalidRefreshTokenException;
import com.eomyoosang.oauth2.user.domain.EmailAccount;
import com.eomyoosang.oauth2.user.domain.User;
import com.eomyoosang.oauth2.user.domain.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenRefreshService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenStore refreshTokenStore;
    private final UserRepository userRepository;
    private final Clock clock;

    @Transactional
    public EmailLoginResult refresh(String refreshTokenValue, String requestDeviceId) {
        ParsedJwt parsed = jwtTokenProvider.parse(refreshTokenValue);
        if (parsed.tokenType() != TokenType.REFRESH) {
            throw new InvalidRefreshTokenException();
        }

        String deviceId = parsed.deviceId();
        if (requestDeviceId != null && !requestDeviceId.isBlank()
                && !deviceId.equals(requestDeviceId.trim())) {
            throw new InvalidRefreshTokenException();
        }

        UUID userId = parsed.userId();
        String storedToken = refreshTokenStore.find(userId, deviceId)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (!storedToken.equals(refreshTokenValue)) {
            refreshTokenStore.delete(userId, deviceId);
            throw new InvalidRefreshTokenException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(InvalidRefreshTokenException::new);

        EmailAccount emailAccount = user.getEmailAccount();
        if (emailAccount == null) {
            throw new InvalidRefreshTokenException();
        }

        if (!emailAccount.isVerified()) {
            throw new EmailNotVerifiedException();
        }

        IssuedToken accessToken = jwtTokenProvider.createAccessToken(userId, deviceId);
        IssuedToken newRefreshToken = jwtTokenProvider.createRefreshToken(userId, deviceId);
        refreshTokenStore.store(userId, deviceId, newRefreshToken.value());

        Instant now = clock.instant();

        return new EmailLoginResult(
                accessToken.value(),
                accessToken.remainingSeconds(now),
                newRefreshToken.value(),
                newRefreshToken.remainingSeconds(now),
                deviceId
        );
    }
}
