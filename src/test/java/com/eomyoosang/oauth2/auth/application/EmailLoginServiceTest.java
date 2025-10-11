package com.eomyoosang.oauth2.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.eomyoosang.oauth2.auth.application.command.EmailLoginCommand;
import com.eomyoosang.oauth2.auth.application.result.EmailLoginResult;
import com.eomyoosang.oauth2.auth.exception.EmailNotVerifiedException;
import com.eomyoosang.oauth2.auth.exception.InvalidCredentialsException;
import com.eomyoosang.oauth2.token.application.IssuedToken;
import com.eomyoosang.oauth2.token.application.JwtTokenProvider;
import com.eomyoosang.oauth2.token.application.RefreshTokenStore;
import com.eomyoosang.oauth2.token.application.TokenType;
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
class EmailLoginServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenStore refreshTokenStore;
    @Mock
    private com.eomyoosang.oauth2.support.security.PasswordHasher passwordHasher;
    @Mock
    private Clock clock;

    @InjectMocks
    private EmailLoginService emailLoginService;

    private User verifiedUser;
    private EmailAccount verifiedEmailAccount;
    private UUID userId;

    @BeforeEach
    void setUp() {
        verifiedUser = User.builder()
                .displayName("tester")
                .status(UserStatus.ACTIVE)
                .build();
        verifiedEmailAccount = EmailAccount.builder()
                .email("user@example.com")
                .passwordHash("hashed")
                .verified(true)
                .build();
        verifiedUser.registerEmailAccount(verifiedEmailAccount);
        userId = UUID.randomUUID();
        ReflectionTestUtils.setField(verifiedUser, "id", userId);
    }

    @Test
    @DisplayName("이메일 로그인 성공 시 토큰과 deviceId를 반환한다")
    void shouldReturnTokensWhenLoginSucceeds() {
        Instant now = Instant.parse("2025-01-01T00:00:00Z");
        IssuedToken accessToken = new IssuedToken("access-token", now.plusSeconds(900), TokenType.ACCESS);
        IssuedToken refreshToken = new IssuedToken("refresh-token", now.plusSeconds(1800), TokenType.REFRESH);

        when(userRepository.findByEmailAccount_Email("user@example.com")).thenReturn(Optional.of(verifiedUser));
        when(passwordHasher.matches("plain-pass", "hashed")).thenReturn(true);
        when(passwordHasher.needsRehash("hashed")).thenReturn(false);
        when(refreshTokenStore.generateDeviceId()).thenReturn("generated-device");
        when(jwtTokenProvider.createAccessToken(userId, "generated-device")).thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(userId, "generated-device")).thenReturn(refreshToken);
        when(clock.instant()).thenReturn(now);

        EmailLoginResult result = emailLoginService.login(new EmailLoginCommand("user@example.com", "plain-pass", null));

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        assertThat(result.deviceId()).isEqualTo("generated-device");
        assertThat(result.accessTokenExpiresIn()).isEqualTo(900);
        assertThat(result.refreshTokenExpiresIn()).isEqualTo(1800);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 InvalidCredentialsException 을 던진다")
    void shouldThrowWhenPasswordMismatch() {
        when(userRepository.findByEmailAccount_Email("user@example.com")).thenReturn(Optional.of(verifiedUser));
        when(passwordHasher.matches("wrong", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> emailLoginService.login(new EmailLoginCommand("user@example.com", "wrong", null)))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("이메일이 인증되지 않았으면 EmailNotVerifiedException 을 던진다")
    void shouldThrowWhenEmailNotVerified() {
        EmailAccount unverified = EmailAccount.builder()
                .email("user@example.com")
                .passwordHash("hashed")
                .verified(false)
                .build();
        verifiedUser.registerEmailAccount(unverified);
        when(userRepository.findByEmailAccount_Email("user@example.com")).thenReturn(Optional.of(verifiedUser));

        assertThatThrownBy(() -> emailLoginService.login(new EmailLoginCommand("user@example.com", "plain", null)))
                .isInstanceOf(EmailNotVerifiedException.class);
    }
}
