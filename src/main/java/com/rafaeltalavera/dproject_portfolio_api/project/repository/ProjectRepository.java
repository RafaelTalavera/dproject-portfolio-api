package com.rafaeltalavera.dproject_portfolio_api.project.repository;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("""
            SELECT COUNT(project)
            FROM Project project
            JOIN project.members member
            WHERE member.id = :memberId
              AND project.status NOT IN (
                  com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus.CLOSED,
                  com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus.CANCELED
              )
            """)
    long countActiveProjectsByMemberId(@Param("memberId") UUID memberId);
}
