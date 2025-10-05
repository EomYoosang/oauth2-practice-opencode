package com.eomyoosang.oauth2.support.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

/**
 * Common base to provide a UUID primary key shared by all entities.
 */
@MappedSuperclass
public abstract class PrimaryKeyEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    protected PrimaryKeyEntity() {
    }

    public UUID getId() {
        return id;
    }
}
