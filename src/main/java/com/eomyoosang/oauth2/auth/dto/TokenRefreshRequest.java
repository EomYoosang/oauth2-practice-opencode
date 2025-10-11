package com.eomyoosang.oauth2.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TokenRefreshRequest(
        @NotBlank(message = "refreshToken은 필수입니다.")
        String refreshToken,

        @Size(min = 8, max = 128, message = "deviceId는 8자 이상 128자 이하로 입력하세요.")
        String deviceId
) {
}
