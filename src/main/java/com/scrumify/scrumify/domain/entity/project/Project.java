package com.scrumify.scrumify.domain.entity.project;

import java.time.Instant;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;
import com.scrumify.scrumify.domain.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "projects")
@Getter
public class Project {

    public Project() {
        this.id = UuidCreator.getTimeOrderedEpoch(Instant.now());
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    @Setter
    private User createdBy;

    @Column(nullable = false, length = 128)
    @Setter
    private String name;

    @Column(name = "code_prefix", nullable = false, unique = true, length = 16)
    @Setter
    private String codePrefix;

    @Column(columnDefinition = "text")
    @Setter
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
