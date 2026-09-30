package com.rafaeltalavera.dproject_portfolio_api.audit.repository;
import com.rafaeltalavera.dproject_portfolio_api.audit.domain.ProjectAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface ProjectAuditEventRepository extends JpaRepository<ProjectAuditEvent, UUID> { }
