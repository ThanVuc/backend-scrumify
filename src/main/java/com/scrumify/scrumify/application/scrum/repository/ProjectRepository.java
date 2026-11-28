package com.scrumify.scrumify.application.scrum.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.project.Project;

public interface ProjectRepository {
    Project save(Project project);
    List<Project> findAllByUserId(UUID userId);
    Optional<Project> findDetailByIdAndUserId(UUID projectId, UUID userId);
    Optional<Project> findByIdAndUserIdForUpdate(UUID projectId, UUID userId);
}
