package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scrumify.scrumify.domain.entity.project.Project;

public interface JpaProjectRepository extends JpaRepository<Project, UUID> {
    @Query("""
            select p from Project p
            join Member m on m.project = p
            where m.user.id = :userId
            order by p.createdAt desc
            """)
    List<Project> findAllByMemberUserId(@Param("userId") UUID userId);

    @Query("""
            select p from Project p
            join Member m on m.project = p
            where p.id = :projectId and m.user.id = :userId
            """)
    Optional<Project> findDetailByIdAndMemberUserId(
            @Param("projectId") UUID projectId,
            @Param("userId") UUID userId);
}
