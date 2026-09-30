package com.rafaeltalavera.dproject_portfolio_api.project.dto;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectRisk;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectQueryFilter(
        String name,
        ProjectStatus status,
        UUID managerId,
        LocalDate startDateFrom,
        LocalDate startDateTo,
        ProjectRisk risk
) {
}
