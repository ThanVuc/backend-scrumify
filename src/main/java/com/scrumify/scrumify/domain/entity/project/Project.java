package com.scrumify.scrumify.domain.entity.project;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;
import com.scrumify.scrumify.domain.entity.backlog.Backlog;
import com.scrumify.scrumify.domain.entity.member.Member;
import com.scrumify.scrumify.domain.entity.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "projects")
@Getter
public class Project {

    public Project() {
        this.id = UuidCreator.getTimeOrderedEpoch(Instant.now());
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Project(User createdBy, String name, String codePrefix, String description) {
        this();
        this.createdBy = createdBy;
        this.name = name;
        this.codePrefix = codePrefix;
        this.description = description;
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    @OneToOne(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Backlog backlog;

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    @Setter
    private User createdBy;

    @Column(nullable = false, length = 128)
    @Setter
    private String name;

    @Column(name = "code_prefix", nullable = false, length = 16)
    @Setter
    private String codePrefix;

    @Column(columnDefinition = "text")
    @Setter
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "next_issue_number", nullable = false)
    @Setter
    private Integer nextIssueNumber = 1;

    public void addMember(Member member) {
        members.add(member);
        member.setProject(this);
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
        backlog.setProject(this);
    }
}
