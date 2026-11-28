package com.scrumify.scrumify.application.scrum.repository;

import java.util.Optional;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.backlog.Backlog;

public interface BacklogRepository {
    Backlog save(Backlog backlog);
    Optional<Backlog> findByProjectId(UUID projectId);
}
