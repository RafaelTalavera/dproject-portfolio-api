package com.rafaeltalavera.dproject_portfolio_api.project.domain.policy;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectDeletionPolicyTest {

    @ParameterizedTest
    @EnumSource(value = ProjectStatus.class, names = {"STARTED", "IN_PROGRESS", "CLOSED"})
    void shouldBlockDeletionForProtectedStatuses(ProjectStatus status) {
        assertThat(ProjectDeletionPolicy.canDelete(status)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = ProjectStatus.class, mode = EnumSource.Mode.EXCLUDE, names = {"STARTED", "IN_PROGRESS", "CLOSED"})
    void shouldAllowDeletionForOtherStatuses(ProjectStatus status) {
        assertThat(ProjectDeletionPolicy.canDelete(status)).isTrue();
    }
}
