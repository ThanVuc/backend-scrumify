package com.scrumify.scrumify.domain.entity.worklog;

import java.time.Instant;
import java.math.BigDecimal;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;
import com.scrumify.scrumify.domain.entity.task.Task;
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
@Table(name = "worklogs")
@Getter
public class Worklog {

    public Worklog() {
        this.id = UuidCreator.getTimeOrderedEpoch(Instant.now());
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignee_id", nullable = false)
    private User assignee;

    @Column(name = "time_hours", nullable = false, precision = 10, scale = 2)
    @Setter
    private BigDecimal timeHours;

    @Column(columnDefinition = "text")
    @Setter
    private String description;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
