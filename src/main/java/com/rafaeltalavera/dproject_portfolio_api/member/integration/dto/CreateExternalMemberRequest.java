package com.rafaeltalavera.dproject_portfolio_api.member.integration.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateExternalMemberRequest(
        @NotBlank(message = "O nome do membro é obrigatório.") String name,
        @NotBlank(message = "A atribuição do membro é obrigatória.") String assignment
) {
}
