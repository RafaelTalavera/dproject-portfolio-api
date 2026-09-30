package com.rafaeltalavera.dproject_portfolio_api.project.domain.policy;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectRisk;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectRiskCalculatorTest {

    private static final LocalDate START_DATE = LocalDate.of(2026, 1, 15);

    @Test
    void shouldReturnLowRiskAtLowerBoundaries() {
        ProjectRisk risk = ProjectRiskCalculator.calculate(
                new BigDecimal("100000.00"),
                START_DATE,
                START_DATE.plusMonths(3)
        );

        assertThat(risk).isEqualTo(ProjectRisk.LOW);
    }

    @Test
    void shouldReturnMediumRiskAtUpperBoundaries() {
        ProjectRisk risk = ProjectRiskCalculator.calculate(
                new BigDecimal("500000.00"),
                START_DATE,
                START_DATE.plusMonths(6)
        );

        assertThat(risk).isEqualTo(ProjectRisk.MEDIUM);
    }

    @Test
    void shouldPrioritizeHighRiskForBudgetAboveLimit() {
        ProjectRisk risk = ProjectRiskCalculator.calculate(
                new BigDecimal("500000.01"),
                START_DATE,
                START_DATE.plusMonths(1)
        );

        assertThat(risk).isEqualTo(ProjectRisk.HIGH);
    }

    @Test
    void shouldReturnMediumRiskForDurationAboveThreeMonths() {
        ProjectRisk risk = ProjectRiskCalculator.calculate(
                new BigDecimal("50000.00"),
                START_DATE,
                START_DATE.plusMonths(3).plusDays(1)
        );

        assertThat(risk).isEqualTo(ProjectRisk.MEDIUM);
    }

    @Test
    void shouldReturnHighRiskForDurationAboveSixMonths() {
        ProjectRisk risk = ProjectRiskCalculator.calculate(
                new BigDecimal("50000.00"),
                START_DATE,
                START_DATE.plusMonths(6).plusDays(1)
        );

        assertThat(risk).isEqualTo(ProjectRisk.HIGH);
    }
}
