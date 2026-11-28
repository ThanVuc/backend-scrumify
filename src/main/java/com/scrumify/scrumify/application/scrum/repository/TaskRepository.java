package com.scrumify.scrumify.application.scrum.repository;

import java.util.List;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.task.Task;

public interface TaskRepository {
    List<Task> findActiveByPbiIdOrderByPositionAsc(UUID pbiId);
}
