package com.scrumify.scrumify.application.scrum.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.pbi.Pbi;
import com.scrumify.scrumify.domain.entity.pbi.PbiStatus;
import com.scrumify.scrumify.domain.entity.pbi.PbiType;

public interface PbiRepository {
    long countActiveByProjectId(UUID projectId);
    long countTotalByProjectId(UUID projectId);
    long countBacklogByProjectId(UUID projectId);
    long countDebtByProjectId(UUID projectId);
    Pbi save(Pbi pbi);
    Optional<Pbi> findById(UUID pbiId);
    Optional<Pbi> findActiveById(UUID pbiId);
    List<Pbi> findActiveByBacklogId(UUID backlogId, PbiStatus status, PbiType type, UUID epicId, String search);
}
