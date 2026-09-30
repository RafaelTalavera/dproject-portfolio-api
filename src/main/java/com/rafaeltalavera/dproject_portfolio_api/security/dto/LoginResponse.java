package com.rafaeltalavera.dproject_portfolio_api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token de acesso emitido após autenticação bem-sucedida")
public record LoginResponse(
        
        @Schema(description = "JWT de acesso")
        String accessToken,
        
        @Schema(description = "Tipo de token", example = "Bearer") 
        String tokenType,
        
        @Schema(description = "Validade do token em segundos", example = "3600")
        long expiresIn
) {
}
