package com.scrumify.scrumify.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.scrumify.scrumify.application.scrum.repository.ProjectRepository;
import com.scrumify.scrumify.domain.entity.project.Project;
import com.scrumify.scrumify.infrastructure.persistence.jpa.JpaProjectRepository;

@Repository
public class ProjectRepositoryImpl implements ProjectRepository {
    private final JpaProjectRepository jpaProjectRepository;

    public ProjectRepositoryImpl(JpaProjectRepository jpaProjectRepository) {
        this.jpaProjectRepository = jpaProjectRepository;
    }

    @Override
    public Project save(Project project) {
        return jpaProjectRepository.save(project);
    }

    @Override
    public List<Project> findAllByUserId(UUID userId) {
        return jpaProjectRepository.findAllByMemberUserId(userId);
    }

    @Override
    public Optional<Project> findDetailByIdAndUserId(UUID projectId, UUID userId) {
        return jpaProjectRepository.findDetailByIdAndMemberUserId(projectId, userId);
    }
}
