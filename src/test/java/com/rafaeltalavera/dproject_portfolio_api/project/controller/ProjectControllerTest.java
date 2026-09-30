package com.rafaeltalavera.dproject_portfolio_api.project.controller;

import com.rafaeltalavera.dproject_portfolio_api.common.exception.GlobalExceptionHandler;
import com.rafaeltalavera.dproject_portfolio_api.member.cache.domain.Member;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.Project;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.ProjectQueryFilter;
import com.rafaeltalavera.dproject_portfolio_api.project.exception.ProjectBusinessRuleException;
import com.rafaeltalavera.dproject_portfolio_api.project.exception.ProjectNotFoundException;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.exception.ExternalMemberClientException;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.exception.ExternalMemberNotFoundException;
import com.rafaeltalavera.dproject_portfolio_api.project.service.ProjectService;
import com.rafaeltalavera.dproject_portfolio_api.security.config.SecurityConfig;
import com.rafaeltalavera.dproject_portfolio_api.security.service.DatabaseUserDetailsService;
import com.rafaeltalavera.dproject_portfolio_api.security.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import({SecurityConfig.class, JwtTokenService.class, GlobalExceptionHandler.class})
class ProjectControllerTest {

    private static final UUID PROJECT_ID = UUID.fromString("9ef7b793-7822-4f1e-a452-5b7ce1a5b9f2");
    private static final UUID MANAGER_ID = UUID.fromString("e0a7f672-d0d1-4f28-8c41-9c987008ec00");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenService jwtTokenService;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private DatabaseUserDetailsService databaseUserDetailsService;

    @Test
    void shouldRejectProjectDetailWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/api/v1/projects/{id}", PROJECT_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnDetailedProjectWithManagerAndAllocatedMembers() throws Exception {
        Project project = project();
        when(projectService.findDetailedById(PROJECT_ID)).thenReturn(project);

        mockMvc.perform(get("/api/v1/projects/{id}", PROJECT_ID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenService.generateToken("portfolio.admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PROJECT_ID.toString()))
                .andExpect(jsonPath("$.manager.id").value(MANAGER_ID.toString()))
                .andExpect(jsonPath("$.manager.externalId").value("employee-001"))
                .andExpect(jsonPath("$.members.length()").value(2))
                .andExpect(jsonPath("$.members[0].externalId").value("employee-001"));
    }

    @Test
    void shouldReturnPaginatedProjectSummaries() throws Exception {
        Project project = project();
        when(projectService.findAll(any(ProjectQueryFilter.class), argThat(pageable -> pageable.getSort().isUnsorted())))
                .thenReturn(new PageImpl<>(List.of(project), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/projects?name=teste&page=0&size=20&sort=string")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenService.generateToken("portfolio.admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(PROJECT_ID.toString()))
                .andExpect(jsonPath("$.content[0].managerId").value(MANAGER_ID.toString()))
                .andExpect(jsonPath("$.content[0].risk").value("MEDIUM"));
    }

    @Test
    void shouldReturnNotFoundForUnknownProject() throws Exception {
        when(projectService.findDetailedById(PROJECT_ID)).thenThrow(new ProjectNotFoundException(PROJECT_ID));

        mockMvc.perform(get("/api/v1/projects/{id}", PROJECT_ID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenService.generateToken("portfolio.admin")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturnUnprocessableEntityForBusinessRuleViolation() throws Exception {
        when(projectService.findDetailedById(PROJECT_ID)).thenThrow(new ProjectBusinessRuleException("Regra de teste"));

        mockMvc.perform(get("/api/v1/projects/{id}", PROJECT_ID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenService.generateToken("portfolio.admin")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Regra de teste"));
    }

    @Test
    void shouldReturnBadGatewayWhenExternalMemberApiIsUnavailable() throws Exception {
        when(projectService.findDetailedById(PROJECT_ID))
                .thenThrow(new ExternalMemberClientException("Falha de teste"));

        mockMvc.perform(get("/api/v1/projects/{id}", PROJECT_ID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenService.generateToken("portfolio.admin")))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502));
    }

    @Test
    void shouldReturnUnprocessableEntityForAnUnknownExternalMember() throws Exception {
        when(projectService.findDetailedById(PROJECT_ID))
                .thenThrow(new ExternalMemberNotFoundException("unknown-member", null));

        mockMvc.perform(get("/api/v1/projects/{id}", PROJECT_ID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenService.generateToken("portfolio.admin")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("O membro externo informado não foi encontrado: unknown-member."));
    }

    private Project project() {
        Member manager = member(MANAGER_ID, "employee-001", "Ana Silva");
        Member developer = member(UUID.fromString("96a5f9f0-b5ae-4b76-8d39-cb179a5ec6d8"), "employee-002", "Bruno Souza");
        Project project = Project.create(
                "Projeto de teste", LocalDate.of(2026, 10, 1), LocalDate.of(2027, 1, 1), null,
                new BigDecimal("250000.00"), "DescriÃ§Ã£o", manager, ProjectStatus.ANALYSIS, Set.of(manager, developer)
        );
        try {
            java.lang.reflect.Field id = Project.class.getDeclaredField("id");
            id.setAccessible(true);
            id.set(project, PROJECT_ID);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
        return project;
    }

    private Member member(UUID id, String externalId, String name) {
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(id);
        when(member.getExternalId()).thenReturn(externalId);
        when(member.getName()).thenReturn(name);
        when(member.getAssignment()).thenReturn("funcionÃ¡rio");
        return member;
    }
}
