package com.scrumify.scrumify.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.scrumify.scrumify.application.scrum.repository.SprintRepository;
import com.scrumify.scrumify.domain.entity.sprint.Sprint;
import com.scrumify.scrumify.domain.entity.sprint.SprintStatus;
import com.scrumify.scrumify.infrastructure.persistence.jpa.JpaSprintRepository;

@Repository
public class SprintRepositoryImpl implements SprintRepository {
    private final JpaSprintRepository jpaSprintRepository;

    public SprintRepositoryImpl(JpaSprintRepository jpaSprintRepository) {
        this.jpaSprintRepository = jpaSprintRepository;
    }

    @Override
    public Optional<Sprint> findActiveByProjectId(UUID projectId) {
        return jpaSprintRepository.findFirstByProjectIdAndStatus(projectId, SprintStatus.ACTIVE);
    }

    @Override
    public Optional<Sprint> findByIdAndProjectId(UUID sprintId, UUID projectId) {
        return jpaSprintRepository.findByIdAndProjectId(sprintId, projectId);
    }
}
