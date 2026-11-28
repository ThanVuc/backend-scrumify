package com.scrumify.scrumify.application.scrum.repository;

import java.util.Optional;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.epic.Epic;

public interface EpicRepository {
    Optional<Epic> findActiveByIdAndProjectId(UUID epicId, UUID projectId);
}
