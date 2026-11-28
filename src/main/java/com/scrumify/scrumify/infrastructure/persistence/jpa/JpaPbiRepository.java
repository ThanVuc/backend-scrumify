package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scrumify.scrumify.domain.entity.pbi.Pbi;
import com.scrumify.scrumify.domain.entity.pbi.PbiStatus;
import com.scrumify.scrumify.domain.entity.pbi.PbiType;

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

    @Query("""
            select p from Pbi p
            left join fetch p.epic
            join fetch p.backlog b
            join fetch b.project
            where p.id = :pbiId
            """)
    Optional<Pbi> findByIdWithBacklogAndProject(@Param("pbiId") UUID pbiId);

    @Query("""
            select p from Pbi p
            left join fetch p.epic
            join fetch p.backlog b
            join fetch b.project
            where p.id = :pbiId and p.archivedAt is null
            """)
    Optional<Pbi> findActiveByIdWithBacklogAndProject(@Param("pbiId") UUID pbiId);

    @Query("""
            select p from Pbi p
            left join fetch p.epic
            where p.backlog.id = :backlogId
              and p.archivedAt is null
              and (:status is null or p.status = :status)
              and (:type is null or p.type = :type)
              and (:epicId is null or p.epic.id = :epicId)
              and (:hasSearch = false
                   or lower(p.title) like :searchPattern
                   or lower(p.code) like :searchPattern
                   or lower(coalesce(p.description, '')) like :searchPattern)
            order by
              case when p.moveToTopAt is null then 1 else 0 end asc,
              p.moveToTopAt desc,
              case p.priority
                when com.scrumify.scrumify.domain.entity.pbi.PbiPriority.HIGHEST then 4
                when com.scrumify.scrumify.domain.entity.pbi.PbiPriority.HIGH then 3
                when com.scrumify.scrumify.domain.entity.pbi.PbiPriority.MEDIUM then 2
                when com.scrumify.scrumify.domain.entity.pbi.PbiPriority.LOW then 1
                else 0
              end desc,
              p.createdAt desc
            """)
    List<Pbi> findActiveByBacklogIdWithFilters(
            @Param("backlogId") UUID backlogId,
            @Param("status") PbiStatus status,
            @Param("type") PbiType type,
            @Param("epicId") UUID epicId,
            @Param("hasSearch") boolean hasSearch,
            @Param("searchPattern") String searchPattern);
}
