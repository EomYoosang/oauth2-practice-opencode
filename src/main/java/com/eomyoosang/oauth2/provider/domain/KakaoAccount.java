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
@Table(name = "kakao_accounts")
@DiscriminatorValue("KAKAO")
public class KakaoAccount extends OAuthAccount {

    @Builder
    private KakaoAccount(String providerUserId,
                         String email,
                         String displayName,
                         String profileImageUrl,
                         LocalDateTime linkedAt,
                         LocalDateTime lastLoginAt) {
        super(ProviderType.KAKAO, providerUserId, email, displayName, profileImageUrl, linkedAt, lastLoginAt);
    }
}
