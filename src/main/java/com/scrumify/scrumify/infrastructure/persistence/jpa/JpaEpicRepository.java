package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scrumify.scrumify.domain.entity.epic.Epic;

public interface JpaEpicRepository extends JpaRepository<Epic, UUID> {
    Optional<Epic> findByIdAndProjectIdAndArchivedAtIsNull(UUID epicId, UUID projectId);
}
