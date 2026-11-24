package com.scrumify.scrumify.infrastructure.persistence.jpa;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scrumify.scrumify.domain.entity.member.Member;

public interface JpaMemberRepository extends JpaRepository<Member, UUID> {
    List<Member> findByProjectIdOrderByJoinedAtAsc(UUID projectId);
}
