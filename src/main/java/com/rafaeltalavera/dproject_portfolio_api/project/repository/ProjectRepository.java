package com.rafaeltalavera.dproject_portfolio_api.project.repository;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
}
