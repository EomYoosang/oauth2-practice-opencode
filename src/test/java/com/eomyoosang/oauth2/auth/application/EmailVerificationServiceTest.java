package com.eomyoosang.oauth2.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eomyoosang.oauth2.auth.application.payload.EmailVerificationPayload;
import com.eomyoosang.oauth2.auth.exception.EmailAlreadyVerifiedException;
import com.eomyoosang.oauth2.auth.exception.EmailVerificationTokenNotFoundException;
import com.eomyoosang.oauth2.auth.exception.UserNotFoundException;
import com.eomyoosang.oauth2.user.domain.EmailAccount;
import com.eomyoosang.oauth2.user.domain.User;
import com.eomyoosang.oauth2.user.domain.UserRepository;
import com.eomyoosang.oauth2.user.domain.UserStatus;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private EmailVerificationTokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    private User pendingUser;

    @BeforeEach
    void setUp() {
        pendingUser = User.builder()
                .displayName("tester")
                .status(UserStatus.PENDING)
                .build();

        EmailAccount emailAccount = EmailAccount.builder()
                .email("user@example.com")
                .passwordHash("hash")
                .verified(false)
                .build();

        pendingUser.registerEmailAccount(emailAccount);
    }

    @DisplayName("토큰 발급 시 사용자 ID가 없으면 예외를 던진다")
    @Test
    void issueTokenRequiresPersistedUser() {
        assertThatThrownBy(() -> emailVerificationService.issueToken(pendingUser))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("토큰 검증 성공 시 이메일을 인증하고 사용자 상태를 ACTIVE로 변경한다")
    @Test
    void verifyTokenActivatesUser() throws Exception {
        UUID userId = UUID.randomUUID();
        setId(pendingUser, userId);

        when(tokenService.consumeToken("token"))
                .thenReturn(Optional.of(new EmailVerificationPayload(userId, "user@example.com")));
        when(userRepository.findById(userId)).thenReturn(Optional.of(pendingUser));

        emailVerificationService.verify("token");

        assertThat(pendingUser.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(pendingUser.getEmailAccount().isVerified()).isTrue();
    }

    @DisplayName("이미 인증된 이메일이면 예외를 던진다")
    @Test
    void verifyTokenAlreadyVerified() throws Exception {
        UUID userId = UUID.randomUUID();
        setId(pendingUser, userId);
        pendingUser.getEmailAccount().markVerified();

        when(tokenService.consumeToken("token"))
                .thenReturn(Optional.of(new EmailVerificationPayload(userId, "user@example.com")));
        when(userRepository.findById(userId)).thenReturn(Optional.of(pendingUser));

        assertThatThrownBy(() -> emailVerificationService.verify("token"))
                .isInstanceOf(EmailAlreadyVerifiedException.class);
    }

    @DisplayName("토큰이 없으면 예외를 던진다")
    @Test
    void verifyTokenMissing() {
        when(tokenService.consumeToken("token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailVerificationService.verify("token"))
                .isInstanceOf(EmailVerificationTokenNotFoundException.class);
    }

    @DisplayName("사용자를 찾을 수 없으면 예외를 던진다")
    @Test
    void verifyTokenUserMissing() {
        UUID userId = UUID.randomUUID();
        when(tokenService.consumeToken("token"))
                .thenReturn(Optional.of(new EmailVerificationPayload(userId, "user@example.com")));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailVerificationService.verify("token"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @DisplayName("토큰 발급 시 토큰 서비스가 호출된다")
    @Test
    void issueTokenDelegatesToTokenService() throws Exception {
        UUID userId = UUID.randomUUID();
        setId(pendingUser, userId);
        when(tokenService.createToken(any(UUID.class), anyString())).thenReturn("token-value");

        String token = emailVerificationService.issueToken(pendingUser);

        verify(tokenService).createToken(userId, "user@example.com");
        assertThat(token).isEqualTo("token-value");
    }

    private static void setId(User user, UUID id) throws Exception {
        Field idField = user.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, id);
    }
}
