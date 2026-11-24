package com.scrumify.scrumify.application.scrum.repository;

import java.util.UUID;

public interface PbiRepository {
    long countActiveByProjectId(UUID projectId);
    long countTotalByProjectId(UUID projectId);
    long countBacklogByProjectId(UUID projectId);
    long countDebtByProjectId(UUID projectId);
}
