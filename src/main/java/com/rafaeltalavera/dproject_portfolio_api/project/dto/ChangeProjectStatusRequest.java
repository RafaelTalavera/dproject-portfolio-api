package com.rafaeltalavera.dproject_portfolio_api.project.dto;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ChangeProjectStatusRequest(
        @NotNull(message = "O status de destino é obrigatório.") ProjectStatus status,
        LocalDate actualEndDate
) {
}
