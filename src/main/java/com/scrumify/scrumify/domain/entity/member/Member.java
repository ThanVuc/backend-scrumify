package com.scrumify.scrumify.domain.entity.member;

import java.time.Instant;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;
import com.scrumify.scrumify.domain.entity.project.Project;
import com.scrumify.scrumify.domain.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "members", uniqueConstraints = @UniqueConstraint(
        name = "uq_members_project_id_user_id",
        columnNames = {"project_id", "user_id"}))
@Getter
public class Member {

    public Member() {
        this.id = UuidCreator.getTimeOrderedEpoch(Instant.now());
        this.joinedAt = Instant.now();
    }

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    @Setter
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Setter
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    @Setter
    private MemberRole role;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();
}
