package com.scrumify.scrumify.domain.entity.sprint;

import java.time.Instant;
import java.util.UUID;

import com.github.f4b6a3.uuid.UuidCreator;
import com.scrumify.scrumify.domain.entity.pbi.Pbi;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sprint_pbis", uniqueConstraints = @UniqueConstraint(
        name = "uq_sprint_pbis_sprint_id_pbi_id",
        columnNames = {"sprint_id", "pbi_id"}))
@Getter
public class SprintPbi {

    public SprintPbi() {
        this.id = UuidCreator.getTimeOrderedEpoch(Instant.now());
        this.addedAt = Instant.now();
    }

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sprint_id", nullable = false)
    @Setter
    private Sprint sprint;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pbi_id", nullable = false)
    @Setter
    private Pbi pbi;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt = Instant.now();
}
