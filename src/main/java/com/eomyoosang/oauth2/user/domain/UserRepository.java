package com.eomyoosang.oauth2.user.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmailAccount_Email(String email);

    Optional<User> findByEmailAccount_Email(String email);
}
