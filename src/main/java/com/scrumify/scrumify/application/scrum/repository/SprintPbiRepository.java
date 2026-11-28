package com.scrumify.scrumify.application.scrum.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.sprint.SprintPbi;

public interface SprintPbiRepository {
    SprintPbi save(SprintPbi sprintPbi);
    List<SprintPbi> findCurrentByPbiIdIn(Collection<UUID> pbiIds);
    List<SprintPbi> findCurrentByPbiId(UUID pbiId);
    void deleteAll(List<SprintPbi> sprintPbis);
}
