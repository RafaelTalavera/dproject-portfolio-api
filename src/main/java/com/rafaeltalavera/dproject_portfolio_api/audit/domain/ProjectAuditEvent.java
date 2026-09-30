package com.rafaeltalavera.dproject_portfolio_api.audit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_audit_events")
public class ProjectAuditEvent {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name = "project_id", nullable = false) private UUID projectId;
    @Enumerated(EnumType.STRING) @Column(name = "event_type", nullable = false) private ProjectAuditEventType eventType;
    @Column(name = "occurred_at", nullable = false) private OffsetDateTime occurredAt;
    @Column(nullable = false) private String actor;
    @Column(nullable = false, columnDefinition = "TEXT") private String details;
    protected ProjectAuditEvent() { }
    private ProjectAuditEvent(UUID projectId, ProjectAuditEventType eventType, String actor, String details) {
        this.projectId = projectId; this.eventType = eventType; this.actor = actor; this.details = details; this.occurredAt = OffsetDateTime.now();
    }
    public static ProjectAuditEvent create(UUID projectId, ProjectAuditEventType eventType, String actor, String details) { return new ProjectAuditEvent(projectId, eventType, actor, details); }
}
