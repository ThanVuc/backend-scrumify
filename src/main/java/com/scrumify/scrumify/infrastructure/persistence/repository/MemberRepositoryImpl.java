package com.scrumify.scrumify.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.scrumify.scrumify.application.scrum.repository.MemberRepository;
import com.scrumify.scrumify.domain.entity.member.Member;
import com.scrumify.scrumify.infrastructure.persistence.jpa.JpaMemberRepository;

@Repository
public class MemberRepositoryImpl implements MemberRepository {
    private final JpaMemberRepository jpaMemberRepository;

    public MemberRepositoryImpl(JpaMemberRepository jpaMemberRepository) {
        this.jpaMemberRepository = jpaMemberRepository;
    }

    @Override
    public Member save(Member member) {
        return jpaMemberRepository.save(member);
    }

    @Override
    public List<Member> findByProjectId(UUID projectId) {
        return jpaMemberRepository.findByProjectIdOrderByJoinedAtAsc(projectId);
    }
}
