package com.scrumify.scrumify.application.scrum.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.member.Member;

public interface MemberRepository {
    Member save(Member member);
    List<Member> saveAll(List<Member> members);
    List<Member> findByProjectId(UUID projectId);
    Set<UUID> findUserIdsByProjectIdAndUserIdIn(UUID projectId, Collection<UUID> userIds);
}
