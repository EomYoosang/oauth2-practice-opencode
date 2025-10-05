package com.eomyoosang.oauth2.user.domain;

import com.eomyoosang.oauth2.provider.domain.OAuthAccount;
import com.eomyoosang.oauth2.support.jpa.PrimaryKeyEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_status", columnList = "status"),
                @Index(name = "idx_user_last_login", columnList = "last_login_at")
        }
)
public class User extends PrimaryKeyEntity {

    @Column(name = "display_name", length = 50, nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private UserStatus status;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @OneToOne(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private EmailAccount emailAccount;

    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<OAuthAccount> oauthAccounts = new HashSet<>();

    @Builder
    private User(String displayName, UserStatus status, LocalDateTime joinedAt) {
        this.displayName = Objects.requireNonNull(displayName, "displayName must not be null");
        this.status = status == null ? UserStatus.ACTIVE : status;
        this.joinedAt = joinedAt != null ? joinedAt : LocalDateTime.now();
    }

    public void changeDisplayName(String newDisplayName) {
        this.displayName = Objects.requireNonNull(newDisplayName, "displayName must not be null");
    }

    public void changeStatus(UserStatus newStatus) {
        this.status = Objects.requireNonNull(newStatus, "status must not be null");
    }

    public boolean isActive() {
        return UserStatus.ACTIVE.equals(status);
    }

    public void markLastLogin(LocalDateTime loginAt) {
        this.lastLoginAt = Objects.requireNonNull(loginAt, "loginAt must not be null");
    }

    public void registerEmailAccount(EmailAccount account) {
        Objects.requireNonNull(account, "account must not be null");
        if (this.emailAccount != null) {
            this.emailAccount.detachFromUser();
        }
        this.emailAccount = account;
        account.attachTo(this);
    }

    public void removeEmailAccount() {
        if (this.emailAccount != null) {
            this.emailAccount.detachFromUser();
            this.emailAccount = null;
        }
    }

    public void addOAuthAccount(OAuthAccount account) {
        Objects.requireNonNull(account, "account must not be null");
        account.attachTo(this);
        oauthAccounts.add(account);
    }

    public void removeOAuthAccount(OAuthAccount account) {
        if (account == null) {
            return;
        }
        if (oauthAccounts.remove(account)) {
            account.detachFromUser();
        }
    }

    public Set<OAuthAccount> getOauthAccounts() {
        return Collections.unmodifiableSet(oauthAccounts);
    }
}
