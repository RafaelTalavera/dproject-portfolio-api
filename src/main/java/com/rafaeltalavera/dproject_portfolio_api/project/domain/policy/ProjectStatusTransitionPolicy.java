package com.rafaeltalavera.dproject_portfolio_api.project.domain.policy;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;

import java.util.List;

public final class ProjectStatusTransitionPolicy {

    private static final List<ProjectStatus> NORMAL_SEQUENCE = List.of(
            ProjectStatus.ANALYSIS,
            ProjectStatus.ANALYSIS_COMPLETED,
            ProjectStatus.ANALYSIS_APPROVED,
            ProjectStatus.STARTED,
            ProjectStatus.PLANNED,
            ProjectStatus.IN_PROGRESS,
            ProjectStatus.CLOSED
    );

    private ProjectStatusTransitionPolicy() {
    }

    public static boolean isAllowed(ProjectStatus currentStatus, ProjectStatus targetStatus) {
        if (isTerminal(currentStatus)) {
            return false;
        }

        if (targetStatus == ProjectStatus.CANCELED) {
            return true;
        }

        int currentPosition = NORMAL_SEQUENCE.indexOf(currentStatus);
        return currentPosition >= 0
                && currentPosition + 1 < NORMAL_SEQUENCE.size()
                && NORMAL_SEQUENCE.get(currentPosition + 1) == targetStatus;
    }

    private static boolean isTerminal(ProjectStatus status) {
        return status == ProjectStatus.CLOSED || status == ProjectStatus.CANCELED;
    }
}
