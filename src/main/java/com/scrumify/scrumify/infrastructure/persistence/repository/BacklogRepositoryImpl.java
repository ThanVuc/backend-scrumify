package com.scrumify.scrumify.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import com.scrumify.scrumify.application.scrum.repository.BacklogRepository;
import com.scrumify.scrumify.domain.entity.backlog.Backlog;
import com.scrumify.scrumify.infrastructure.persistence.jpa.JpaBacklogRepository;

@Repository
public class BacklogRepositoryImpl implements BacklogRepository {
    private final JpaBacklogRepository jpaBacklogRepository;

    public BacklogRepositoryImpl(JpaBacklogRepository jpaBacklogRepository) {
        this.jpaBacklogRepository = jpaBacklogRepository;
    }

    @Override
    public Backlog save(Backlog backlog) {
        return jpaBacklogRepository.save(backlog);
    }
}
