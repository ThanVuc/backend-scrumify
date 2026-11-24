package com.scrumify.scrumify.domain.entity.sprint;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;
import com.scrumify.scrumify.domain.entity.project.Project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sprints")
@Getter
public class Sprint {

    public Sprint() {
        this.id = UuidCreator.getTimeOrderedEpoch(Instant.now());
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 32)
    @Setter
    private String code;

    @Column(nullable = false, length = 128)
    @Setter
    private String name;

    @Column(columnDefinition = "text")
    @Setter
    private String goal;

    @Column(nullable = false, length = 32)
    @Setter
    private String status;

    @Column(name = "start_date")
    @Setter
    private LocalDate startDate;

    @Column(name = "end_date")
    @Setter
    private LocalDate endDate;

    @Column(name = "completed_at")
    @Setter
    private Instant completedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
