package com.eomyoosang.oauth2.user.domain;

import java.util.UUID;
import org.springframework.data.repository.Repository;

public interface UserRepository extends Repository<User, UUID> {

    boolean existsByEmail(String email);

    User save(User user);
}
