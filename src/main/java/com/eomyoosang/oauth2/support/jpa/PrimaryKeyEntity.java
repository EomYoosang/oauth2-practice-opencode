package com.eomyoosang.oauth2.support.jpa;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Common base to provide an auto-generated numeric primary key.
 */
@MappedSuperclass
public abstract class PrimaryKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    protected PrimaryKeyEntity() {
    }

    public Long getId() {
        return id;
    }
}
