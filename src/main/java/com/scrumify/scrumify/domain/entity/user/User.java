package com.scrumify.scrumify.domain.entity.user;

import java.time.Instant;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
public class User {

    public User(
            String fullName,
            String email,
            String passwordHash,
            UserRole roles,
            UserStatus status,
            String avatarUrl) {
        this.id = UuidCreator.getTimeOrderedEpoch(Instant.now());
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles;
        this.status = status;
        this.avatarUrl = avatarUrl;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public User() {
        this.id = UuidCreator.getTimeOrderedEpoch(Instant.now());
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 128)
    @Setter
    private String fullName;

    @Column(nullable = false, unique = true, length = 256)
    @Setter
    private String email;

    @Column(name = "password_hash", nullable = false, length = 128)
    @Setter
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 256)
    @Setter
    private UserRole roles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    @Setter
    private UserStatus status;

    @Column(name = "avatar_url", length = 256)
    @Setter
    private String avatarUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "last_login_at")
    @Setter
    private Instant lastLoginAt;
}
