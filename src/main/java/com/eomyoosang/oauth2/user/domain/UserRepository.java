package com.eomyoosang.oauth2.user.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User, UUID> {

    boolean existsByEmailAccount_Email(String email);

    User save(User user);

    Optional<User> findById(UUID id);
}
