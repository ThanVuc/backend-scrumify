package com.scrumify.scrumify.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.scrumify.scrumify.application.scrum.repository.EpicRepository;
import com.scrumify.scrumify.domain.entity.epic.Epic;
import com.scrumify.scrumify.infrastructure.persistence.jpa.JpaEpicRepository;

@Repository
public class EpicRepositoryImpl implements EpicRepository {
    private final JpaEpicRepository jpaEpicRepository;

    public EpicRepositoryImpl(JpaEpicRepository jpaEpicRepository) {
        this.jpaEpicRepository = jpaEpicRepository;
    }

    @Override
    public Optional<Epic> findActiveByIdAndProjectId(UUID epicId, UUID projectId) {
        return jpaEpicRepository.findByIdAndProjectIdAndArchivedAtIsNull(epicId, projectId);
    }
}
