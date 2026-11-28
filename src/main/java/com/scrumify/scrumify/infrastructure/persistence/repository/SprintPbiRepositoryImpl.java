package com.scrumify.scrumify.infrastructure.persistence.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.scrumify.scrumify.application.scrum.repository.SprintPbiRepository;
import com.scrumify.scrumify.domain.entity.sprint.SprintPbi;
import com.scrumify.scrumify.infrastructure.persistence.jpa.JpaSprintPbiRepository;

@Repository
public class SprintPbiRepositoryImpl implements SprintPbiRepository {
    private final JpaSprintPbiRepository jpaSprintPbiRepository;

    public SprintPbiRepositoryImpl(JpaSprintPbiRepository jpaSprintPbiRepository) {
        this.jpaSprintPbiRepository = jpaSprintPbiRepository;
    }

    @Override
    public SprintPbi save(SprintPbi sprintPbi) {
        return jpaSprintPbiRepository.save(sprintPbi);
    }

    @Override
    public List<SprintPbi> findCurrentByPbiIdIn(Collection<UUID> pbiIds) {
        return jpaSprintPbiRepository.findCurrentByPbiIdIn(pbiIds);
    }

    @Override
    public List<SprintPbi> findCurrentByPbiId(UUID pbiId) {
        return jpaSprintPbiRepository.findCurrentByPbiId(pbiId);
    }

    @Override
    public void deleteAll(List<SprintPbi> sprintPbis) {
        jpaSprintPbiRepository.deleteAll(sprintPbis);
    }
}
