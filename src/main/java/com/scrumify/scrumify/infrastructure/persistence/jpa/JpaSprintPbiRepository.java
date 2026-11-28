package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scrumify.scrumify.domain.entity.sprint.SprintPbi;

public interface JpaSprintPbiRepository extends JpaRepository<SprintPbi, UUID> {
    @Query("""
            select sp from SprintPbi sp
            join fetch sp.sprint s
            where sp.pbi.id in :pbiIds
              and s.status <> com.scrumify.scrumify.domain.entity.sprint.SprintStatus.COMPLETED
            order by sp.addedAt desc
            """)
    List<SprintPbi> findCurrentByPbiIdIn(@Param("pbiIds") Collection<UUID> pbiIds);

    @Query("""
            select sp from SprintPbi sp
            join fetch sp.sprint s
            where sp.pbi.id = :pbiId
              and s.status <> com.scrumify.scrumify.domain.entity.sprint.SprintStatus.COMPLETED
            order by sp.addedAt desc
            """)
    List<SprintPbi> findCurrentByPbiId(@Param("pbiId") UUID pbiId);
}
