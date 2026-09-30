package com.rafaeltalavera.dproject_portfolio_api.report.service;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;
import com.rafaeltalavera.dproject_portfolio_api.project.repository.ProjectRepository;
import com.rafaeltalavera.dproject_portfolio_api.report.dto.PortfolioSummaryResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PortfolioReportServiceTest {

    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final PortfolioReportService reportService = new PortfolioReportService(projectRepository);

    @Test
    void shouldReturnDatabaseAggregatesForPortfolioSummary() {
        ProjectRepository.PortfolioStatusAggregate analysis = aggregate("ANALYSIS", 2, "150000.00");
        ProjectRepository.PortfolioStatusAggregate closed = aggregate("CLOSED", 1, "300000.00");
        when(projectRepository.summarizeByStatus()).thenReturn(List.of(analysis, closed));
        when(projectRepository.calculateAverageClosedDurationDays()).thenReturn(new BigDecimal("42.50"));
        when(projectRepository.countDistinctAllocatedMembers()).thenReturn(3L);

        PortfolioSummaryResponse response = reportService.summarize();

        assertThat(response.projectsByStatus()).containsExactly(
                new com.rafaeltalavera.dproject_portfolio_api.report.dto.PortfolioStatusSummary(
                        ProjectStatus.ANALYSIS, 2, new BigDecimal("150000.00")
                ),
                new com.rafaeltalavera.dproject_portfolio_api.report.dto.PortfolioStatusSummary(
                        ProjectStatus.CLOSED, 1, new BigDecimal("300000.00")
                )
        );
        assertThat(response.averageClosedDurationDays()).isEqualByComparingTo("42.50");
        assertThat(response.distinctAllocatedMembers()).isEqualTo(3L);
    }

    @Test
    void shouldReturnZeroWhenThereAreNoClosedProjects() {
        when(projectRepository.summarizeByStatus()).thenReturn(List.of());
        when(projectRepository.calculateAverageClosedDurationDays()).thenReturn(null);
        when(projectRepository.countDistinctAllocatedMembers()).thenReturn(0L);

        PortfolioSummaryResponse response = reportService.summarize();

        assertThat(response.projectsByStatus()).isEmpty();
        assertThat(response.averageClosedDurationDays()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.distinctAllocatedMembers()).isZero();
    }

    private ProjectRepository.PortfolioStatusAggregate aggregate(String status, long count, String totalBudget) {
        ProjectRepository.PortfolioStatusAggregate aggregate = mock(ProjectRepository.PortfolioStatusAggregate.class);
        when(aggregate.getStatus()).thenReturn(status);
        when(aggregate.getProjectCount()).thenReturn(count);
        when(aggregate.getTotalBudget()).thenReturn(new BigDecimal(totalBudget));
        return aggregate;
    }
}
