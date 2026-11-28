package com.scrumify.scrumify.infrastructure.persistence.repository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.scrumify.scrumify.application.scrum.repository.PbiRepository;
import com.scrumify.scrumify.domain.entity.pbi.Pbi;
import com.scrumify.scrumify.domain.entity.pbi.PbiStatus;
import com.scrumify.scrumify.domain.entity.pbi.PbiType;
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

    @Override
    public Pbi save(Pbi pbi) {
        return jpaPbiRepository.save(pbi);
    }

    @Override
    public Optional<Pbi> findById(UUID pbiId) {
        return jpaPbiRepository.findByIdWithBacklogAndProject(pbiId);
    }

    @Override
    public Optional<Pbi> findActiveById(UUID pbiId) {
        return jpaPbiRepository.findActiveByIdWithBacklogAndProject(pbiId);
    }

    @Override
    public List<Pbi> findActiveByBacklogId(
            UUID backlogId, PbiStatus status, PbiType type, UUID epicId, String search) {
        boolean hasSearch = search != null && !search.isBlank();
        String searchPattern = hasSearch ? "%" + search.trim().toLowerCase(Locale.ROOT) + "%" : "";
        return jpaPbiRepository.findActiveByBacklogIdWithFilters(
                backlogId, status, type, epicId, hasSearch, searchPattern);
    }
}
