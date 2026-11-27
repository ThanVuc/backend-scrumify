package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scrumify.scrumify.domain.entity.user.User;
import com.scrumify.scrumify.domain.entity.user.UserStatus;

public interface JpaUserRepository
    extends JpaRepository<User, UUID> {
        boolean existsByEmail(String email);
        Optional<User> findByEmail(String email);

        @Query("select u from User u where lower(u.email) in :emails and u.status = :status")
        List<User> findAllByLowerEmailInAndStatus(
                @Param("emails") Collection<String> emails,
                @Param("status") UserStatus status);
}
