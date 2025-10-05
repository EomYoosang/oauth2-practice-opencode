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
@Table(name = "google_accounts")
@DiscriminatorValue("GOOGLE")
public class GoogleAccount extends OAuthAccount {

    @Builder
    private GoogleAccount(String providerUserId,
                          String email,
                          String displayName,
                          String profileImageUrl,
                          LocalDateTime linkedAt,
                          LocalDateTime lastLoginAt) {
        super(ProviderType.GOOGLE, providerUserId, email, displayName, profileImageUrl, linkedAt, lastLoginAt);
    }
}
