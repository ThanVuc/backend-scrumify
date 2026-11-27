package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.scrumify.scrumify.domain.entity.member.Member;

public interface JpaMemberRepository extends JpaRepository<Member, UUID> {
    List<Member> findByProjectIdOrderByJoinedAtAsc(UUID projectId);

    @Query("select m.user.id from Member m where m.project.id = :projectId and m.user.id in :userIds")
    Set<UUID> findUserIdsByProjectIdAndUserIdIn(
            @Param("projectId") UUID projectId,
            @Param("userIds") Collection<UUID> userIds);
}
