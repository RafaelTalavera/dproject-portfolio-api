package com.rafaeltalavera.dproject_portfolio_api.project.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record UpdateProjectRequest(
        @NotBlank(message = "O nome do projeto é obrigatório.") @Size(max = 255, message = "O nome do projeto deve ter no máximo 255 caracteres.") String name,
        @NotNull(message = "A data de início é obrigatória.") LocalDate startDate,
        @NotNull(message = "A previsão de término é obrigatória.") LocalDate expectedEndDate,
        LocalDate actualEndDate,
        @NotNull(message = "O orçamento total é obrigatório.") @DecimalMin(value = "0.01", message = "O orçamento total deve ser maior que zero.") BigDecimal totalBudget,
        String description,
        @NotBlank(message = "O identificador externo do gerente é obrigatório.") String managerExternalId,
        @NotEmpty(message = "O projeto deve possuir ao menos um membro.") @Size(max = 10, message = "O projeto pode possuir no máximo 10 membros.") List<@NotBlank(message = "O identificador externo do membro é obrigatório.") String> memberExternalIds
) {
}
