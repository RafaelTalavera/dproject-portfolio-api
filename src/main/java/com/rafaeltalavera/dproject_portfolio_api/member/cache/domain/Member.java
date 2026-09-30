package com.rafaeltalavera.dproject_portfolio_api.member.cache.domain;

import com.rafaeltalavera.dproject_portfolio_api.member.integration.dto.ExternalMemberResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String assignment;

    @Column(name = "synced_at", nullable = false)
    private OffsetDateTime syncedAt;

    protected Member() {
    }

    private Member(ExternalMemberResponse response) {
        updateSnapshot(response);
    }

    public static Member from(ExternalMemberResponse response) {
        return new Member(response);
    }

    public void updateSnapshot(ExternalMemberResponse response) {
        this.externalId = response.id();
        this.name = response.name();
        this.assignment = response.assignment();
        this.syncedAt = OffsetDateTime.now();
    }

    public String getExternalId() {
        return externalId;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAssignment() {
        return assignment;
    }
}
