package com.eomyoosang.oauth2.auth.presentation;

import com.eomyoosang.oauth2.auth.application.EmailLoginService;
import com.eomyoosang.oauth2.auth.application.TokenRefreshService;
import com.eomyoosang.oauth2.auth.application.command.EmailLoginCommand;
import com.eomyoosang.oauth2.auth.dto.EmailLoginRequest;
import com.eomyoosang.oauth2.auth.dto.EmailLoginResponse;
import com.eomyoosang.oauth2.auth.dto.TokenRefreshRequest;
import com.eomyoosang.oauth2.auth.application.result.EmailLoginResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailLoginController {

    private final EmailLoginService emailLoginService;
    private final TokenRefreshService tokenRefreshService;

    @PostMapping("/auth/login/email")
    public ResponseEntity<EmailLoginResponse> login(@Valid @RequestBody EmailLoginRequest request) {
        EmailLoginCommand command = new EmailLoginCommand(
                request.email(),
                request.password(),
                request.deviceId()
        );
        EmailLoginResult result = emailLoginService.login(command);
        return ResponseEntity.ok(EmailLoginResponse.from(result));
    }

    @PostMapping("/auth/token/refresh")
    public ResponseEntity<EmailLoginResponse> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        EmailLoginResult result = tokenRefreshService.refresh(request.refreshToken(), request.deviceId());
        return ResponseEntity.ok(EmailLoginResponse.from(result));
    }
}
