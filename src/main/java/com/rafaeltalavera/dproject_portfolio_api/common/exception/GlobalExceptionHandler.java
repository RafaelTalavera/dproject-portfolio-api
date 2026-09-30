package com.rafaeltalavera.dproject_portfolio_api.common.exception;

import com.rafaeltalavera.dproject_portfolio_api.project.exception.ProjectBusinessRuleException;
import com.rafaeltalavera.dproject_portfolio_api.project.exception.ProjectNotFoundException;
import com.rafaeltalavera.dproject_portfolio_api.security.exception.InvalidCredentialsException;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.exception.ExternalMemberClientException;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.exception.ExternalMemberNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ApiError> notFound(ProjectNotFoundException exception, WebRequest request) { return error(HttpStatus.NOT_FOUND, "N\u00e3o encontrado", exception.getMessage(), request); }
    @ExceptionHandler(ProjectBusinessRuleException.class)
    public ResponseEntity<ApiError> business(ProjectBusinessRuleException exception, WebRequest request) { return error(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de neg\u00f3cio", exception.getMessage(), request); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception, WebRequest request) {
        String message = exception.getBindingResult().getFieldErrors().stream().findFirst().map(error -> error.getDefaultMessage()).orElse("A requisi\u00e7\u00e3o possui dados inv\u00e1lidos.");
        return error(HttpStatus.BAD_REQUEST, "Requisi\u00e7\u00e3o inv\u00e1lida", message, request);
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> credentials(InvalidCredentialsException exception, WebRequest request) { return error(HttpStatus.UNAUTHORIZED, "N\u00e3o autorizado", exception.getMessage(), request); }
    @ExceptionHandler(ExternalMemberNotFoundException.class)
    public ResponseEntity<ApiError> externalMemberNotFound(ExternalMemberNotFoundException exception, WebRequest request) { return error(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negócio", exception.getMessage(), request); }
    @ExceptionHandler(ExternalMemberClientException.class)
    public ResponseEntity<ApiError> externalMemberUnavailable(ExternalMemberClientException exception, WebRequest request) { return error(HttpStatus.BAD_GATEWAY, "Integração indisponível", "Não foi possível concluir a operação com a API externa de membros.", request); }
    private ResponseEntity<ApiError> error(HttpStatus status, String error, String message, WebRequest request) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), error, message, request.getDescription(false).replace("uri=", "")));
    }
}
