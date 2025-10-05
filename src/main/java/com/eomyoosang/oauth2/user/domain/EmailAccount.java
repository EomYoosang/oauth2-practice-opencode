package com.eomyoosang.oauth2.user.domain;

import com.eomyoosang.oauth2.support.jpa.PrimaryKeyEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
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
@Table(name = "email_accounts")
public class EmailAccount extends PrimaryKeyEntity {

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder
    private EmailAccount(String email,
                         String passwordHash,
                         boolean verified,
                         LocalDateTime verifiedAt,
                         LocalDateTime registeredAt,
                         LocalDateTime lastLoginAt,
                         LocalDateTime passwordChangedAt) {
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        this.verified = verified;
        this.verifiedAt = verifiedAt;
        this.registeredAt = registeredAt != null ? registeredAt : LocalDateTime.now();
        this.lastLoginAt = lastLoginAt;
        this.passwordChangedAt = passwordChangedAt;
    }

    public void updatePassword(String encodedPassword, LocalDateTime changedAt) {
        this.passwordHash = Objects.requireNonNull(encodedPassword, "encodedPassword must not be null");
        this.passwordChangedAt = Objects.requireNonNull(changedAt, "changedAt must not be null");
    }

    public void markVerified(LocalDateTime verifiedAt) {
        this.verified = true;
        this.verifiedAt = Objects.requireNonNull(verifiedAt, "verifiedAt must not be null");
    }

    public void recordLogin(LocalDateTime loginAt) {
        this.lastLoginAt = Objects.requireNonNull(loginAt, "loginAt must not be null");
    }

    void attachTo(User user) {
        if (this.user != null && !this.user.equals(user)) {
            throw new IllegalStateException("Email account is already assigned to another user");
        }
        this.user = Objects.requireNonNull(user, "user must not be null");
    }

    void detachFromUser() {
        this.user = null;
    }
}
