package com.eomyoosang.oauth2.user.domain;

import com.eomyoosang.oauth2.support.jpa.PrimaryKeyEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_status", columnList = "status")
})
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

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private EmailAccount emailAccount;

    @Builder
    private User(String displayName, UserStatus status, LocalDateTime joinedAt) {
        this.displayName = Objects.requireNonNull(displayName, "displayName must not be null");
        this.status = status == null ? UserStatus.ACTIVE : status;
        this.joinedAt = joinedAt != null ? joinedAt : LocalDateTime.now();
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

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void recordSuccessfulLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
