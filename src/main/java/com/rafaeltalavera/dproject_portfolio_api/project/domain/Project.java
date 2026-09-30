package com.rafaeltalavera.dproject_portfolio_api.project.domain;

import com.rafaeltalavera.dproject_portfolio_api.member.cache.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "expected_end_date", nullable = false)
    private LocalDate expectedEndDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @Column(name = "total_budget", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalBudget;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    private Member manager;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ProjectStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_members",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private Set<Member> members = new LinkedHashSet<>();

    @Column(name = "created_at", nullable = false) private OffsetDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private OffsetDateTime updatedAt;
    @Column(name = "created_by", nullable = false) private String createdBy;
    @Column(name = "updated_by", nullable = false) private String updatedBy;

    protected Project() {
    }

    private Project(
            String name,
            LocalDate startDate,
            LocalDate expectedEndDate,
            LocalDate actualEndDate,
            BigDecimal totalBudget,
            String description,
            Member manager,
            ProjectStatus status,
            Set<Member> members
    ) {
        this.name = name;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.actualEndDate = actualEndDate;
        this.totalBudget = totalBudget;
        this.description = description;
        this.manager = manager;
        this.status = status;
        this.members = new LinkedHashSet<>(members);
    }

    public static Project create(
            String name,
            LocalDate startDate,
            LocalDate expectedEndDate,
            LocalDate actualEndDate,
            BigDecimal totalBudget,
            String description,
            Member manager,
            ProjectStatus status,
            Set<Member> members
    ) {
        return new Project(
                name,
                startDate,
                expectedEndDate,
                actualEndDate,
                totalBudget,
                description,
                manager,
                status,
                members
        );
    }

    public UUID getId() {
        return id;
    }

    public void updateDetails(
            String name, LocalDate startDate, LocalDate expectedEndDate, LocalDate actualEndDate,
            BigDecimal totalBudget, String description, Member manager, Set<Member> members
    ) {
        this.name = name;
        this.startDate = startDate;
        this.expectedEndDate = expectedEndDate;
        this.actualEndDate = actualEndDate;
        this.totalBudget = totalBudget;
        this.description = description;
        this.manager = manager;
        this.members = new LinkedHashSet<>(members);
    }

    public void changeStatus(ProjectStatus status, LocalDate actualEndDate) {
        this.status = status;
        if (status == ProjectStatus.CLOSED) {
            this.actualEndDate = actualEndDate;
        }
    }

    public void initializeAudit(String actor) { OffsetDateTime now = OffsetDateTime.now(); this.createdAt = now; this.updatedAt = now; this.createdBy = actor; this.updatedBy = actor; }
    public void touchAudit(String actor) { this.updatedAt = OffsetDateTime.now(); this.updatedBy = actor; }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getExpectedEndDate() {
        return expectedEndDate;
    }

    public LocalDate getActualEndDate() {
        return actualEndDate;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public String getDescription() {
        return description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public Member getManager() {
        return manager;
    }

    public Set<Member> getMembers() {
        return Collections.unmodifiableSet(members);
    }
}
