package com.eomyoosang.oauth2.provider.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "apple_accounts")
@DiscriminatorValue("APPLE")
public class AppleAccount extends OAuthAccount {

    @Builder
    private AppleAccount(String providerUserId,
                         String email,
                         String displayName,
                         String profileImageUrl,
                         LocalDateTime linkedAt,
                         LocalDateTime lastLoginAt) {
        super(ProviderType.APPLE, providerUserId, email, displayName, profileImageUrl, linkedAt, lastLoginAt);
    }
}
