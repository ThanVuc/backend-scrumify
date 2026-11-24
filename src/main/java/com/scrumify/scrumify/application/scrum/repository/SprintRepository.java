package com.scrumify.scrumify.application.scrum.repository;

import java.util.Optional;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.sprint.Sprint;
import com.scrumify.scrumify.domain.entity.sprint.SprintStatus;

public interface SprintRepository {
    Optional<Sprint> findActiveByProjectId(UUID projectId);
}
