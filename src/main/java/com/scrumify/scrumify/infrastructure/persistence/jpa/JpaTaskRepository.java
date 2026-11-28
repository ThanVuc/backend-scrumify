package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scrumify.scrumify.domain.entity.task.Task;

public interface JpaTaskRepository extends JpaRepository<Task, UUID> {
    @Query("""
            select t from Task t
            left join fetch t.assignee
            where t.pbi.id = :pbiId and t.archivedAt is null
            order by t.position asc, t.createdAt asc
            """)
    List<Task> findActiveByPbiIdOrderByPositionAsc(@Param("pbiId") UUID pbiId);
}
