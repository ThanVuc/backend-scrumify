package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scrumify.scrumify.domain.entity.backlog.Backlog;

public interface JpaBacklogRepository extends JpaRepository<Backlog, UUID> {
    Optional<Backlog> findByProjectId(UUID projectId);
}
