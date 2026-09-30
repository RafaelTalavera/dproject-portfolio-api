package com.rafaeltalavera.dproject_portfolio_api.member.integration.exception;

public class ExternalMemberNotFoundException extends RuntimeException {

    public ExternalMemberNotFoundException(String externalId, Throwable cause) {
        super("O membro externo informado não foi encontrado: " + externalId + ".", cause);
    }
}
