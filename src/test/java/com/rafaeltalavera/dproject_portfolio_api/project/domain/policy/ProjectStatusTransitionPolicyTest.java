package com.rafaeltalavera.dproject_portfolio_api.project.domain.policy;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectStatusTransitionPolicyTest {

    @Test
    void shouldAllowOnlyTheNextNormalStatus() {
        assertThat(ProjectStatusTransitionPolicy.isAllowed(
                ProjectStatus.ANALYSIS,
                ProjectStatus.ANALYSIS_COMPLETED
        )).isTrue();
    }

    @Test
    void shouldRejectSkippedAndBackwardStatuses() {
        assertThat(ProjectStatusTransitionPolicy.isAllowed(
                ProjectStatus.ANALYSIS,
                ProjectStatus.ANALYSIS_APPROVED
        )).isFalse();
        assertThat(ProjectStatusTransitionPolicy.isAllowed(
                ProjectStatus.PLANNED,
                ProjectStatus.STARTED
        )).isFalse();
    }

    @Test
    void shouldAllowCancellationFromAnyStatusExceptCanceled() {
        assertThat(ProjectStatusTransitionPolicy.isAllowed(
                ProjectStatus.ANALYSIS_COMPLETED,
                ProjectStatus.CANCELED
        )).isTrue();
        assertThat(ProjectStatusTransitionPolicy.isAllowed(
                ProjectStatus.IN_PROGRESS,
                ProjectStatus.CANCELED
        )).isTrue();
        assertThat(ProjectStatusTransitionPolicy.isAllowed(
                ProjectStatus.CLOSED,
                ProjectStatus.CANCELED
        )).isTrue();
    }

    @Test
    void shouldRejectTransitionsFromCanceledStatus() {
        assertThat(ProjectStatusTransitionPolicy.isAllowed(
                ProjectStatus.CANCELED,
                ProjectStatus.ANALYSIS
        )).isFalse();
    }
}
