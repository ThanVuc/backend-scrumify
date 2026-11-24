package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scrumify.scrumify.domain.entity.sprint.Sprint;

public interface JpaSprintRepository extends JpaRepository<Sprint, UUID> {
    Optional<Sprint> findFirstByProjectIdAndStatus(
            UUID projectId,
            com.scrumify.scrumify.domain.entity.sprint.SprintStatus status);
}
