package com.eomyoosang.oauth2.auth.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.eomyoosang.oauth2.auth.application.EmailLoginService;
import com.eomyoosang.oauth2.auth.application.TokenRefreshService;
import com.eomyoosang.oauth2.auth.application.command.EmailLoginCommand;
import com.eomyoosang.oauth2.auth.application.result.EmailLoginResult;
import com.eomyoosang.oauth2.auth.dto.TokenRefreshRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EmailLoginController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
class EmailLoginControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailLoginService emailLoginService;

    @MockBean
    private TokenRefreshService tokenRefreshService;

    @Test
    @DisplayName("이메일 로그인 성공 시 토큰 정보를 반환한다")
    void shouldReturnTokensOnLogin() throws Exception {
        when(emailLoginService.login(any(EmailLoginCommand.class)))
                .thenReturn(new EmailLoginResult("access", 900, "refresh", 1800, "device-1"));

        mockMvc.perform(post("/auth/login/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "password": "Password123!",
                                  "deviceId": "device-1"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"))
                .andExpect(jsonPath("$.deviceId").value("device-1"));
    }

    @Test
    @DisplayName("리프레시 토큰 회전 성공 시 새로운 토큰 정보를 반환한다")
    void shouldReturnTokensOnRefresh() throws Exception {
        when(tokenRefreshService.refresh("refresh", "device-1"))
                .thenReturn(new EmailLoginResult("new-access", 900, "new-refresh", 1800, "device-1"));

        TokenRefreshRequest request = new TokenRefreshRequest("refresh", "device-1");

        mockMvc.perform(post("/auth/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"))
                .andExpect(jsonPath("$.deviceId").value("device-1"));
    }
}
