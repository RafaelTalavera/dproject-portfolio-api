package com.rafaeltalavera.dproject_portfolio_api.report.service;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;
import com.rafaeltalavera.dproject_portfolio_api.project.repository.ProjectRepository;
import com.rafaeltalavera.dproject_portfolio_api.report.dto.PortfolioStatusSummary;
import com.rafaeltalavera.dproject_portfolio_api.report.dto.PortfolioSummaryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PortfolioReportService {

    private final ProjectRepository projectRepository;

    public PortfolioReportService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public PortfolioSummaryResponse summarize() {
        List<PortfolioStatusSummary> projectsByStatus = projectRepository.summarizeByStatus().stream()
                .map(aggregate -> new PortfolioStatusSummary(
                        ProjectStatus.valueOf(aggregate.getStatus()),
                        aggregate.getProjectCount(),
                        aggregate.getTotalBudget()
                ))
                .toList();

        BigDecimal averageClosedDurationDays = projectRepository.calculateAverageClosedDurationDays();
        return new PortfolioSummaryResponse(
                projectsByStatus,
                averageClosedDurationDays == null ? BigDecimal.ZERO : averageClosedDurationDays,
                projectRepository.countDistinctAllocatedMembers()
        );
    }
}
