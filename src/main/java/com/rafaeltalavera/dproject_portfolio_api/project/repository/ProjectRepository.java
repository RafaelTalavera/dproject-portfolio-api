package com.rafaeltalavera.dproject_portfolio_api.project.repository;

import com.rafaeltalavera.dproject_portfolio_api.project.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = """
            SELECT p.* FROM portfolio.projects p
            WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:name AS text), '%')))
              AND (CAST(:status AS text) IS NULL OR p.status = CAST(:status AS text))
              AND (CAST(:managerId AS uuid) IS NULL OR p.manager_id = CAST(:managerId AS uuid))
              AND (CAST(:startDateFrom AS date) IS NULL OR p.start_date >= CAST(:startDateFrom AS date))
              AND (CAST(:startDateTo AS date) IS NULL OR p.start_date <= CAST(:startDateTo AS date))
              AND (CAST(:risk AS text) IS NULL OR
                    (CAST(:risk AS text) = 'HIGH' AND (p.total_budget > 500000 OR p.expected_end_date > p.start_date + INTERVAL '6 months')) OR
                    (CAST(:risk AS text) = 'MEDIUM' AND p.total_budget <= 500000 AND p.expected_end_date <= p.start_date + INTERVAL '6 months'
                        AND (p.total_budget > 100000 OR p.expected_end_date > p.start_date + INTERVAL '3 months')) OR
                    (CAST(:risk AS text) = 'LOW' AND p.total_budget <= 100000 AND p.expected_end_date <= p.start_date + INTERVAL '3 months'))
            """, countQuery = """
            SELECT COUNT(*) FROM portfolio.projects p
            WHERE (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:name AS text), '%')))
              AND (CAST(:status AS text) IS NULL OR p.status = CAST(:status AS text))
              AND (CAST(:managerId AS uuid) IS NULL OR p.manager_id = CAST(:managerId AS uuid))
              AND (CAST(:startDateFrom AS date) IS NULL OR p.start_date >= CAST(:startDateFrom AS date))
              AND (CAST(:startDateTo AS date) IS NULL OR p.start_date <= CAST(:startDateTo AS date))
              AND (CAST(:risk AS text) IS NULL OR
                    (CAST(:risk AS text) = 'HIGH' AND (p.total_budget > 500000 OR p.expected_end_date > p.start_date + INTERVAL '6 months')) OR
                    (CAST(:risk AS text) = 'MEDIUM' AND p.total_budget <= 500000 AND p.expected_end_date <= p.start_date + INTERVAL '6 months'
                        AND (p.total_budget > 100000 OR p.expected_end_date > p.start_date + INTERVAL '3 months')) OR
                    (CAST(:risk AS text) = 'LOW' AND p.total_budget <= 100000 AND p.expected_end_date <= p.start_date + INTERVAL '3 months'))
            """, nativeQuery = true)
    Page<Project> findByFilter(
            @Param("name") String name,
            @Param("status") String status,
            @Param("managerId") UUID managerId,
            @Param("startDateFrom") java.time.LocalDate startDateFrom,
            @Param("startDateTo") java.time.LocalDate startDateTo,
            @Param("risk") String risk,
            Pageable pageable
    );
}
