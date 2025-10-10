package com.eomyoosang.oauth2.support.security;

import com.eomyoosang.oauth2.config.security.PasswordProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PasswordPolicyValidator {

    @Autowired
    private PasswordProperties properties;

    @Autowired
    private CompromisedPasswordChecker compromisedPasswordChecker;

    private final Pattern uppercasePattern = Pattern.compile(".*[A-Z].*");
    private final Pattern lowercasePattern = Pattern.compile(".*[a-z].*");
    private final Pattern digitPattern = Pattern.compile(".*[0-9].*");

    public PasswordValidationResult validate(CharSequence rawPassword) {
        Objects.requireNonNull(rawPassword, "rawPassword must not be null");

        String password = rawPassword.toString();
        List<String> violations = new ArrayList<>();
        PasswordProperties.Policy policy = properties.getPolicy();

        if (password.length() < policy.getMinimumLength()) {
            violations.add("비밀번호는 최소 %d자 이상이어야 합니다.".formatted(policy.getMinimumLength()));
        }
        if (policy.isRequireUppercase() && !uppercasePattern.matcher(password).matches()) {
            violations.add("대문자를 최소 1자 포함해야 합니다.");
        }
        if (policy.isRequireLowercase() && !lowercasePattern.matcher(password).matches()) {
            violations.add("소문자를 최소 1자 포함해야 합니다.");
        }
        if (policy.isRequireDigit() && !digitPattern.matcher(password).matches()) {
            violations.add("숫자를 최소 1자 포함해야 합니다.");
        }
        if (policy.isRequireSpecial()) {
            String specials = policy.getSpecialCharacters();
            if (password.chars().noneMatch(ch -> specials.indexOf(ch) >= 0)) {
                violations.add("다음 특수문자 중 하나 이상을 포함해야 합니다: %s".formatted(specials));
            }
        }

        if (compromisedPasswordChecker.isCompromised(password)) {
            violations.add("유출된 비밀번호로 확인되어 사용할 수 없습니다. (provider: %s)".formatted(compromisedPasswordChecker.provider()));
        }

        if (violations.isEmpty()) {
            return PasswordValidationResult.success();
        }
        return PasswordValidationResult.failure(violations);
    }

    public void validateOrThrow(CharSequence rawPassword) {
        PasswordValidationResult result = validate(rawPassword);
        if (!result.valid()) {
            throw new InvalidPasswordException(result.violations());
        }
    }
}
