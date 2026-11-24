package com.scrumify.scrumify.domain.entity.task;

import java.time.Instant;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;
import com.scrumify.scrumify.domain.entity.pbi.Pbi;
import com.scrumify.scrumify.domain.entity.user.User;

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
@Table(name = "tasks")
@Getter
public class Task {

    public Task() {
        this.id = UuidCreator.getTimeOrderedEpoch(Instant.now());
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pbi_id", nullable = false)
    private Pbi pbi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    @Setter
    private User assignee;

    @Column(nullable = false, length = 256)
    @Setter
    private String title;

    @Column(columnDefinition = "text")
    @Setter
    private String description;

    @Column(nullable = false, length = 32)
    @Setter
    private String status;

    @Column(name = "is_debt", nullable = false)
    @Setter
    private boolean debt;

    @Column(name = "start_date")
    @Setter
    private LocalDate startDate;

    @Column(name = "due_date")
    @Setter
    private LocalDate dueDate;

    @Column(nullable = false)
    @Setter
    private Integer priority;

    @Column(nullable = false)
    @Setter
    private Integer position = 0;

    @Column(name = "estimated_time_hour", precision = 10, scale = 2)
    @Setter
    private BigDecimal estimatedTimeHour;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "archived_at")
    @Setter
    private Instant archivedAt;
}
