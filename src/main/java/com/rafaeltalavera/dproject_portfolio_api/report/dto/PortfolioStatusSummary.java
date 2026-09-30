package com.rafaeltalavera.dproject_portfolio_api.report.dto;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;

import java.math.BigDecimal;

public record PortfolioStatusSummary(ProjectStatus status, long projectCount, BigDecimal totalBudget) {
}
