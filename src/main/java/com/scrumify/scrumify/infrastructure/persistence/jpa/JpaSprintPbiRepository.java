package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scrumify.scrumify.domain.entity.sprint.SprintPbi;

public interface JpaSprintPbiRepository extends JpaRepository<SprintPbi, UUID> {
}
