package com.rafaeltalavera.dproject_portfolio_api.audit.service;
import com.rafaeltalavera.dproject_portfolio_api.audit.domain.ProjectAuditEvent;
import com.rafaeltalavera.dproject_portfolio_api.audit.domain.ProjectAuditEventType;
import com.rafaeltalavera.dproject_portfolio_api.audit.repository.ProjectAuditEventRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.UUID;
@Service
public class ProjectAuditService {
    private final ProjectAuditEventRepository repository;
    public ProjectAuditService(ProjectAuditEventRepository repository) { this.repository = repository; }
    public String currentActor() { Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); return authentication == null ? "system" : authentication.getName(); }
    public void record(UUID projectId, ProjectAuditEventType type, String details) { repository.save(ProjectAuditEvent.create(projectId, type, currentActor(), details)); }
}
