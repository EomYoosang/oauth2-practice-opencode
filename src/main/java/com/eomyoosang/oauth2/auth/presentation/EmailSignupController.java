package com.eomyoosang.oauth2.auth.presentation;

import com.eomyoosang.oauth2.auth.application.EmailSignupService;
import com.eomyoosang.oauth2.auth.application.command.EmailSignupCommand;
import com.eomyoosang.oauth2.auth.application.result.EmailSignupResult;
import com.eomyoosang.oauth2.auth.dto.EmailSignupRequest;
import com.eomyoosang.oauth2.auth.dto.EmailSignupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailSignupController {

    private final EmailSignupService emailSignupService;

    @PostMapping("/auth/register/email")
    public ResponseEntity<EmailSignupResponse> register(@Valid @RequestBody EmailSignupRequest request) {
        EmailSignupResult result = emailSignupService.register(new EmailSignupCommand(request.email(), request.password(), request.name()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EmailSignupResponse.success(result.verificationToken()));
    }
}
