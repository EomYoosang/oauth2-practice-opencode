package com.eomyoosang.oauth2.auth.application.payload;

import java.util.UUID;

public record EmailVerificationPayload(UUID userId, String email) {
}
