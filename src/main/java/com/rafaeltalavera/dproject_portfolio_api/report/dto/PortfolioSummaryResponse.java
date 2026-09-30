package com.rafaeltalavera.dproject_portfolio_api.report.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioSummaryResponse(
        List<PortfolioStatusSummary> projectsByStatus,
        BigDecimal averageClosedDurationDays,
        long distinctAllocatedMembers
) {
}
