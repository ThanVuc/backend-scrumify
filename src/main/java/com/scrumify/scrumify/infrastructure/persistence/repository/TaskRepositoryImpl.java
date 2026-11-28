package com.scrumify.scrumify.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.scrumify.scrumify.application.scrum.repository.TaskRepository;
import com.scrumify.scrumify.domain.entity.task.Task;
import com.scrumify.scrumify.infrastructure.persistence.jpa.JpaTaskRepository;

@Repository
public class TaskRepositoryImpl implements TaskRepository {
    private final JpaTaskRepository jpaTaskRepository;

    public TaskRepositoryImpl(JpaTaskRepository jpaTaskRepository) {
        this.jpaTaskRepository = jpaTaskRepository;
    }

    @Override
    public List<Task> findActiveByPbiIdOrderByPositionAsc(UUID pbiId) {
        return jpaTaskRepository.findActiveByPbiIdOrderByPositionAsc(pbiId);
    }
}
