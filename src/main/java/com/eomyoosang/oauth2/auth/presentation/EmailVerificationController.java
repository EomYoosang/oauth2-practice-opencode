package com.eomyoosang.oauth2.auth.presentation;

import com.eomyoosang.oauth2.auth.application.EmailVerificationService;
import com.eomyoosang.oauth2.auth.dto.EmailVerificationResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<EmailVerificationResponse> verify(@RequestParam("token") @NotBlank String token) {
        emailVerificationService.verify(token);
        return ResponseEntity.ok(EmailVerificationResponse.success());
    }
}
