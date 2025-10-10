package com.eomyoosang.oauth2.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eomyoosang.oauth2.auth.application.command.EmailSignupCommand;
import com.eomyoosang.oauth2.auth.exception.EmailAlreadyRegisteredException;
import com.eomyoosang.oauth2.support.security.InvalidPasswordException;
import com.eomyoosang.oauth2.user.application.EmailAccountRegistrationService;
import com.eomyoosang.oauth2.user.domain.EmailAccount;
import com.eomyoosang.oauth2.user.domain.User;
import com.eomyoosang.oauth2.user.domain.UserRepository;
import com.eomyoosang.oauth2.user.domain.UserStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailSignupServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailAccountRegistrationService registrationService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private EmailSignupService emailSignupService;

    private EmailSignupCommand command;

    @BeforeEach
    void setUp() {
        command = new EmailSignupCommand("user@example.com", "StrongPass123!", "테스터");
    }

    @Test
    void shouldRegisterUserWhenEmailAvailable() {
        when(userRepository.existsByEmailAccount_Email(command.email())).thenReturn(false);
        when(registrationService.register(any(User.class), any(), any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.registerEmailAccount(EmailAccount.builder()
                    .email(command.email())
                    .passwordHash("encoded")
                    .verified(false)
                    .registeredAt(LocalDateTime.now())
                    .build());
            return user.getEmailAccount();
        });
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(emailVerificationService.issueToken(any(User.class))).thenReturn("token");

        var result = emailSignupService.register(command);

        assertThat(result.user().getDisplayName()).isEqualTo("테스터");
        assertThat(result.user().getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.user().getEmailAccount()).isNotNull();
        assertThat(result.verificationToken()).isEqualTo("token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmailAccount_Email(command.email())).thenReturn(true);

        assertThatThrownBy(() -> emailSignupService.register(command))
                .isInstanceOf(EmailAlreadyRegisteredException.class);

        verify(registrationService, never()).register(any(), any(), any());
        verify(userRepository, never()).save(any());
        verify(emailVerificationService, never()).issueToken(any());
    }

    @Test
    void shouldPropagateInvalidPasswordException() {
        when(userRepository.existsByEmailAccount_Email(command.email())).thenReturn(false);
        doThrow(new InvalidPasswordException(List.of("정책 위반")))
                .when(registrationService).register(any(User.class), any(), any());

        assertThatThrownBy(() -> emailSignupService.register(command))
                .isInstanceOf(InvalidPasswordException.class);

        verify(userRepository, never()).save(any());
        verify(emailVerificationService, never()).issueToken(any());
    }
}
