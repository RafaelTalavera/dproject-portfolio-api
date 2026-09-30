package com.rafaeltalavera.dproject_portfolio_api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais para autenticação na API")
public record LoginRequest(

        @Schema(description = "Nome de usuário", example = "portfolio.admin")
        @NotBlank(message = "O usuário é obrigatório.")
        String username,
        
        @Schema(description = "Senha do usuário", example = "password")
        @NotBlank(message = "A senha é obrigatória.")
        String password
) {
}
