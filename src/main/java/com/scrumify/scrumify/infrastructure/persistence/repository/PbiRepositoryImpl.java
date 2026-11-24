package com.scrumify.scrumify.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.scrumify.scrumify.application.scrum.repository.PbiRepository;
import com.scrumify.scrumify.infrastructure.persistence.jpa.JpaPbiRepository;

@Repository
public class PbiRepositoryImpl implements PbiRepository {
    private final JpaPbiRepository jpaPbiRepository;

    public PbiRepositoryImpl(JpaPbiRepository jpaPbiRepository) {
        this.jpaPbiRepository = jpaPbiRepository;
    }

    @Override
    public long countActiveByProjectId(UUID projectId) {
        return jpaPbiRepository.countActiveByProjectId(projectId);
    }

    @Override
    public long countTotalByProjectId(UUID projectId) {
        return jpaPbiRepository.countTotalByProjectId(projectId);
    }

    @Override
    public long countBacklogByProjectId(UUID projectId) {
        return jpaPbiRepository.countBacklogByProjectId(projectId);
    }

    @Override
    public long countDebtByProjectId(UUID projectId) {
        return jpaPbiRepository.countDebtByProjectId(projectId);
    }
}
