package com.rafaeltalavera.dproject_portfolio_api.member.integration.exception;

public class ExternalMemberClientException extends RuntimeException {

    public ExternalMemberClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalMemberClientException(String message) {
        super(message);
    }
}
