package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scrumify.scrumify.domain.entity.user.User;

public interface JpaUserRepository
    extends JpaRepository<User, UUID> {
        boolean existsByEmail(String email);
        Optional<User> findByEmail(String email);
}
