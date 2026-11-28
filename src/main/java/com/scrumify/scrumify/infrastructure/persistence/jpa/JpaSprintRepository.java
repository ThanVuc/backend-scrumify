package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scrumify.scrumify.domain.entity.sprint.Sprint;
import com.scrumify.scrumify.domain.entity.sprint.SprintStatus;

public interface JpaSprintRepository extends JpaRepository<Sprint, UUID> {
    Optional<Sprint> findFirstByProjectIdAndStatus(UUID projectId, SprintStatus status);
    Optional<Sprint> findByIdAndProjectId(UUID sprintId, UUID projectId);
}
