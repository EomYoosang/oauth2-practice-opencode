package com.eomyoosang.oauth2.provider.domain;

import com.eomyoosang.oauth2.support.jpa.PrimaryKeyEntity;
import com.eomyoosang.oauth2.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "oauth_accounts",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_provider_account", columnNames = {"provider_type", "provider_user_id"})
        },
        indexes = {
                @Index(name = "idx_oauth_user", columnList = "user_id")
        }
)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "account_type")
public abstract class OAuthAccount extends PrimaryKeyEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", length = 20, nullable = false)
    private ProviderType providerType;

    @Column(name = "provider_user_id", length = 64, nullable = false)
    private String providerUserId;

    @Column(name = "email", length = 320)
    private String email;

    @Column(name = "display_name", length = 80)
    private String displayName;

    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;

    @Column(name = "linked_at", nullable = false)
    private LocalDateTime linkedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    protected OAuthAccount(ProviderType providerType,
                           String providerUserId,
                           String email,
                           String displayName,
                           String profileImageUrl,
                           LocalDateTime linkedAt,
                           LocalDateTime lastLoginAt) {
        this.providerType = Objects.requireNonNull(providerType, "providerType must not be null");
        this.providerUserId = Objects.requireNonNull(providerUserId, "providerUserId must not be null");
        this.email = email;
        this.displayName = displayName;
        this.profileImageUrl = profileImageUrl;
        this.linkedAt = linkedAt != null ? linkedAt : LocalDateTime.now();
        this.lastLoginAt = lastLoginAt;
    }

    public void attachTo(User user) {
        Objects.requireNonNull(user, "user must not be null");
        if (this.user != null && !this.user.equals(user)) {
            throw new IllegalStateException("OAuth account is already attached to another user");
        }
        this.user = user;
    }

    public void detachFromUser() {
        this.user = null;
    }

    public void recordLogin(LocalDateTime loginAt) {
        this.lastLoginAt = Objects.requireNonNull(loginAt, "loginAt must not be null");
    }

    public void updateProfile(String displayName, String profileImageUrl) {
        this.displayName = displayName;
        this.profileImageUrl = profileImageUrl;
    }
}
