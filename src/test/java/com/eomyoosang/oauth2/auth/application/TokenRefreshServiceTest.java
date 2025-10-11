package com.eomyoosang.oauth2.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

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
import com.eomyoosang.oauth2.user.domain.UserStatus;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TokenRefreshServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenStore refreshTokenStore;
    @Mock
    private UserRepository userRepository;
    @Mock
    private Clock clock;

    @InjectMocks
    private TokenRefreshService tokenRefreshService;

    private UUID userId;
    private User user;
    private EmailAccount emailAccount;
    private ParsedJwt parsedJwt;
    private Instant now;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .displayName("tester")
                .status(UserStatus.ACTIVE)
                .build();
        emailAccount = EmailAccount.builder()
                .email("user@example.com")
                .passwordHash("hashed")
                .verified(true)
                .build();
        user.registerEmailAccount(emailAccount);
        ReflectionTestUtils.setField(user, "id", userId);

        parsedJwt = new ParsedJwt("refresh-token", TokenType.REFRESH, userId, "device-1", Instant.parse("2025-01-01T00:30:00Z"));
        now = Instant.parse("2025-01-01T00:00:00Z");
    }

    @Test
    @DisplayName("리프레시 토큰 회전 성공 시 새로운 토큰을 반환한다")
    void shouldRotateRefreshToken() {
        IssuedToken newAccess = new IssuedToken("new-access", now.plusSeconds(900), TokenType.ACCESS);
        IssuedToken newRefresh = new IssuedToken("new-refresh", now.plusSeconds(1800), TokenType.REFRESH);

        when(jwtTokenProvider.parse("refresh-token")).thenReturn(parsedJwt);
        when(refreshTokenStore.find(userId, "device-1")).thenReturn(Optional.of("refresh-token"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.createAccessToken(userId, "device-1")).thenReturn(newAccess);
        when(jwtTokenProvider.createRefreshToken(userId, "device-1")).thenReturn(newRefresh);
        when(clock.instant()).thenReturn(now);

        EmailLoginResult result = tokenRefreshService.refresh("refresh-token", "device-1");

        assertThat(result.accessToken()).isEqualTo("new-access");
        assertThat(result.refreshToken()).isEqualTo("new-refresh");
        assertThat(result.deviceId()).isEqualTo("device-1");
        assertThat(result.accessTokenExpiresIn()).isEqualTo(900);
        assertThat(result.refreshTokenExpiresIn()).isEqualTo(1800);
    }

    @Test
    @DisplayName("deviceId가 일치하지 않으면 InvalidRefreshTokenException 을 던진다")
    void shouldThrowWhenDeviceMismatch() {
        when(jwtTokenProvider.parse("refresh-token")).thenReturn(parsedJwt);

        assertThatThrownBy(() -> tokenRefreshService.refresh("refresh-token", "other-device"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    @DisplayName("이메일이 인증되지 않은 경우 EmailNotVerifiedException 을 던진다")
    void shouldThrowWhenEmailNotVerified() {
        emailAccount = EmailAccount.builder()
                .email("user@example.com")
                .passwordHash("hashed")
                .verified(false)
                .build();
        user.registerEmailAccount(emailAccount);

        when(jwtTokenProvider.parse("refresh-token")).thenReturn(parsedJwt);
        when(refreshTokenStore.find(userId, "device-1")).thenReturn(Optional.of("refresh-token"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> tokenRefreshService.refresh("refresh-token", "device-1"))
                .isInstanceOf(EmailNotVerifiedException.class);
    }
}
