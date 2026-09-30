package com.rafaeltalavera.dproject_portfolio_api.security.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Credenciais inválidas.");
    }
}
