package com.scrumify.scrumify.application.scrum.repository;

import java.util.List;
import java.util.UUID;

import com.scrumify.scrumify.domain.entity.member.Member;

public interface MemberRepository {
    Member save(Member member);
    List<Member> findByProjectId(UUID projectId);
}
