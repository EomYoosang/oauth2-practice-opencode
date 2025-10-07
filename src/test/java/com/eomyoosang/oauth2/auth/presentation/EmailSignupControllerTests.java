package com.eomyoosang.oauth2.auth.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eomyoosang.oauth2.auth.application.EmailSignupService;
import com.eomyoosang.oauth2.auth.application.command.EmailSignupCommand;
import com.eomyoosang.oauth2.auth.application.result.EmailSignupResult;
import com.eomyoosang.oauth2.auth.exception.EmailAlreadyRegisteredException;
import com.eomyoosang.oauth2.support.security.InvalidPasswordException;
import com.eomyoosang.oauth2.user.domain.User;
import com.eomyoosang.oauth2.user.domain.UserStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EmailSignupController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class EmailSignupControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailSignupService emailSignupService;

    @Test
    @DisplayName("정상 회원가입 요청은 201을 반환한다")
    void shouldReturnCreatedWhenSignupSucceeds() throws Exception {
        User user = User.builder()
                .displayName("테스터")
                .status(UserStatus.PENDING)
                .joinedAt(LocalDateTime.now())
                .build();
        when(emailSignupService.register(any(EmailSignupCommand.class)))
                .thenReturn(new EmailSignupResult(user, "token-value"));

        mockMvc.perform(post("/auth/register/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignupPayload("user@example.com", "StrongPass123!", "테스터"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.message").value("회원가입 성공, 인증 메일을 확인하세요."))
                .andExpect(jsonPath("$.verificationToken").value("token-value"));
    }

    @Test
    @DisplayName("잘못된 입력은 400과 검증 에러 코드를 반환한다")
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        mockMvc.perform(post("/auth/register/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignupPayload("invalid", "short", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("A1000"));
    }

    @Test
    @DisplayName("이미 존재하는 이메일은 409와 U1000 에러 코드를 반환한다")
    void shouldReturnConflictWhenEmailDuplicated() throws Exception {
        doThrow(new EmailAlreadyRegisteredException())
                .when(emailSignupService).register(any(EmailSignupCommand.class));

        mockMvc.perform(post("/auth/register/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignupPayload("user@example.com", "StrongPass123!", "테스터"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("U1000"));
    }

    @Test
    @DisplayName("비밀번호 정책 위반은 400과 A1001 에러 코드를 반환한다")
    void shouldReturnBadRequestWhenPasswordInvalid() throws Exception {
        doThrow(new InvalidPasswordException(List.of("정책 위반")))
                .when(emailSignupService).register(any(EmailSignupCommand.class));

        mockMvc.perform(post("/auth/register/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SignupPayload("user@example.com", "weakpass", "테스터"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("A1001"));
    }

    private record SignupPayload(String email, String password, String name) {
    }
}
