package com.rafaeltalavera.dproject_portfolio_api.project.domain.policy;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectRisk;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class ProjectRiskCalculator {

    private static final BigDecimal MEDIUM_RISK_BUDGET_LIMIT = new BigDecimal("100000.00");
    private static final BigDecimal HIGH_RISK_BUDGET_LIMIT = new BigDecimal("500000.00");

    private ProjectRiskCalculator() {
    }

    public static ProjectRisk calculate(
            BigDecimal totalBudget,
            LocalDate startDate,
            LocalDate expectedEndDate
    ) {
        if (totalBudget.compareTo(HIGH_RISK_BUDGET_LIMIT) > 0
                || expectedEndDate.isAfter(startDate.plusMonths(6))) {
            return ProjectRisk.HIGH;
        }

        if (totalBudget.compareTo(MEDIUM_RISK_BUDGET_LIMIT) > 0
                || expectedEndDate.isAfter(startDate.plusMonths(3))) {
            return ProjectRisk.MEDIUM;
        }

        return ProjectRisk.LOW;
    }
}
