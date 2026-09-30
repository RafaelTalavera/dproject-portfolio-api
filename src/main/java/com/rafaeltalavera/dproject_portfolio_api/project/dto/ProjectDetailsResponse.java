package com.rafaeltalavera.dproject_portfolio_api.project.dto;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectRisk;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProjectDetailsResponse(
        UUID id,
        String name,
        LocalDate startDate,
        LocalDate expectedEndDate,
        LocalDate actualEndDate,
        BigDecimal totalBudget,
        String description,
        MemberResponse manager,
        ProjectStatus status,
        ProjectRisk risk,
        List<MemberResponse> members
) {
}
