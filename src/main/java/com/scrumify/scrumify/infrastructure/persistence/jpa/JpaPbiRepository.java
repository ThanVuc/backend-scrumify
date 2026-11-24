package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scrumify.scrumify.domain.entity.pbi.Pbi;

public interface JpaPbiRepository extends JpaRepository<Pbi, UUID> {
    @Query("""
            select count(p) from Pbi p
            where p.backlog.project.id = :projectId
              and p.archivedAt is null
              and p.id in (
                  select sp.pbi.id from SprintPbi sp
                  join sp.sprint s
                  where s.project.id = :projectId
                    and s.status = com.scrumify.scrumify.domain.entity.sprint.SprintStatus.ACTIVE
              )
            """)
    long countActiveByProjectId(@Param("projectId") UUID projectId);

    @Query("""
            select count(p) from Pbi p
            where p.backlog.project.id = :projectId and p.archivedAt is null
            """)
    long countTotalByProjectId(@Param("projectId") UUID projectId);

    @Query("""
            select count(p) from Pbi p
            where p.backlog.project.id = :projectId
              and p.archivedAt is null
              and p.id not in (
                  select sp.pbi.id from SprintPbi sp
                  join sp.sprint s
                  where s.project.id = :projectId
                    and s.status = com.scrumify.scrumify.domain.entity.sprint.SprintStatus.ACTIVE
              )
            """)
    long countBacklogByProjectId(@Param("projectId") UUID projectId);

    @Query("""
            select count(p) from Pbi p
            where p.backlog.project.id = :projectId
              and p.archivedAt is null
              and p.type = com.scrumify.scrumify.domain.entity.pbi.PbiType.TECHNICAL
            """)
    long countDebtByProjectId(@Param("projectId") UUID projectId);
}
