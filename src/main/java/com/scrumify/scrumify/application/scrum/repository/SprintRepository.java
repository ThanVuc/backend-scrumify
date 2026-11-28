package com.scrumify.scrumify.application.scrum.repository;

import java.util.Optional;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.sprint.Sprint;

public interface SprintRepository {
    Optional<Sprint> findActiveByProjectId(UUID projectId);
    Optional<Sprint> findByIdAndProjectId(UUID sprintId, UUID projectId);
}
