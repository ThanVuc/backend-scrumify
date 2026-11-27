package com.scrumify.scrumify.application.auth.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.user.User;
import com.scrumify.scrumify.domain.entity.user.UserStatus;

public interface UserRepository {
    boolean existsByEmail(String email);
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    List<User> findAllByEmailInIgnoreCaseAndStatus(Collection<String> emails, UserStatus status);
}
