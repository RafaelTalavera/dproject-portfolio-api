package com.rafaeltalavera.dproject_portfolio_api.project.exception;

import java.util.UUID;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(UUID projectId) {
        super("Projeto não encontrado: " + projectId);
    }
}
