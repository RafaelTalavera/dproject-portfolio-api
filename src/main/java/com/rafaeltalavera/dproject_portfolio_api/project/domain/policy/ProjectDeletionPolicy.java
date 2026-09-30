package com.rafaeltalavera.dproject_portfolio_api.project.domain.policy;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;

import java.util.Set;

public final class ProjectDeletionPolicy {

    private static final Set<ProjectStatus> BLOCKED_STATUSES = Set.of(
            ProjectStatus.STARTED,
            ProjectStatus.IN_PROGRESS,
            ProjectStatus.CLOSED
    );

    private ProjectDeletionPolicy() {
    }

    public static boolean canDelete(ProjectStatus status) {
        return !BLOCKED_STATUSES.contains(status);
    }
}
