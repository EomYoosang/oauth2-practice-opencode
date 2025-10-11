package com.eomyoosang.oauth2.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailLoginRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 128, message = "비밀번호는 8자 이상 128자 이하로 입력하세요.")
        String password,

        @Size(min = 8, max = 128, message = "deviceId는 8자 이상 128자 이하로 입력하세요.")
        String deviceId
) {
}
