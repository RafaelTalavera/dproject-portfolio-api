package com.rafaeltalavera.dproject_portfolio_api.project.service;

import com.rafaeltalavera.dproject_portfolio_api.member.cache.domain.Member;
import com.rafaeltalavera.dproject_portfolio_api.member.cache.repository.MemberRepository;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.service.MemberIntegrationService;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.Project;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.CreateProjectRequest;
import com.rafaeltalavera.dproject_portfolio_api.project.exception.ProjectBusinessRuleException;
import com.rafaeltalavera.dproject_portfolio_api.project.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    private final ProjectRepository projectRepository = mock(ProjectRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final MemberIntegrationService memberIntegrationService = mock(MemberIntegrationService.class);
    private final Member manager = member("employee-001", "funcionário");
    private final Member developer = member("employee-002", "funcionário");

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository, memberRepository, memberIntegrationService);
        when(memberRepository.findByExternalId(anyString())).thenAnswer(invocation -> Optional.ofNullable(
                Map.of(
                        manager.getExternalId(), manager,
                        developer.getExternalId(), developer
                ).get(invocation.getArgument(0))
        ));
        when(projectRepository.countActiveProjectsByMemberId(any(UUID.class))).thenReturn(0L);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldCreateProjectAfterSynchronizingAllocatedMembers() {
        Project project = projectService.create(validRequest());

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture());
        verify(memberIntegrationService).findByExternalId("employee-001");
        verify(memberIntegrationService).findByExternalId("employee-002");
        assertThat(project).isSameAs(projectCaptor.getValue());
        assertThat(project.getManager()).isSameAs(manager);
        assertThat(project.getMembers()).containsExactlyInAnyOrder(manager, developer);
        assertThat(project.getStatus()).isEqualTo(ProjectStatus.ANALYSIS);
    }

    @Test
    void shouldRejectDuplicateMembersBeforeSynchronization() {
        CreateProjectRequest request = request(
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2027, 1, 1),
                null,
                ProjectStatus.ANALYSIS,
                "employee-001",
                List.of("employee-001", "employee-001")
        );

        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(ProjectBusinessRuleException.class)
                .hasMessage("A lista de membros não pode conter duplicidades.");

        verify(memberIntegrationService, never()).findByExternalId(anyString());
    }

    @Test
    void shouldRejectManagerWhoIsNotAllocated() {
        CreateProjectRequest request = request(
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2027, 1, 1),
                null,
                ProjectStatus.ANALYSIS,
                "employee-002",
                List.of("employee-001")
        );

        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(ProjectBusinessRuleException.class)
                .hasMessage("O gerente deve fazer parte dos membros alocados.");
    }

    @Test
    void shouldRejectInitialStatusOtherThanAnalysis() {
        CreateProjectRequest request = request(
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2027, 1, 1),
                null,
                ProjectStatus.PLANNED,
                "employee-001",
                List.of("employee-001")
        );

        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(ProjectBusinessRuleException.class)
                .hasMessage("Um projeto deve ser criado no status ANALYSIS.");
    }

    @Test
    void shouldRejectMoreThanTenMembers() {
        List<String> members = java.util.stream.IntStream.range(0, 11)
                .mapToObj(index -> "employee-%03d".formatted(index))
                .toList();
        CreateProjectRequest request = request(
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2027, 1, 1),
                null,
                ProjectStatus.ANALYSIS,
                "employee-000",
                members
        );

        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(ProjectBusinessRuleException.class)
                .hasMessage("O projeto deve possuir entre um e dez membros.");
    }

    @Test
    void shouldRejectMemberWhoIsNotAnEmployee() {
        Member consultant = member("consultant-001", "consultor");
        when(memberRepository.findByExternalId("consultant-001")).thenReturn(Optional.of(consultant));
        CreateProjectRequest request = request(
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2027, 1, 1),
                null,
                ProjectStatus.ANALYSIS,
                "consultant-001",
                List.of("consultant-001")
        );

        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(ProjectBusinessRuleException.class)
                .hasMessage("Somente membros com atribuição funcionário podem ser associados ao projeto.");
    }

    @Test
    void shouldRejectInvalidDateRange() {
        CreateProjectRequest request = request(
                LocalDate.of(2026, 10, 2),
                LocalDate.of(2026, 10, 1),
                null,
                ProjectStatus.ANALYSIS,
                "employee-001",
                List.of("employee-001")
        );

        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(ProjectBusinessRuleException.class)
                .hasMessage("A previsão de término deve ser igual ou posterior à data de início.");
    }

    @Test
    void shouldRejectMemberWhoAlreadyHasThreeActiveProjects() {
        when(projectRepository.countActiveProjectsByMemberId(manager.getId())).thenReturn(3L);

        assertThatThrownBy(() -> projectService.create(validRequest()))
                .isInstanceOf(ProjectBusinessRuleException.class)
                .hasMessage("Um membro não pode estar alocado em mais de três projetos ativos.");
    }

    private CreateProjectRequest validRequest() {
        return request(
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2027, 1, 1),
                null,
                ProjectStatus.ANALYSIS,
                "employee-001",
                List.of("employee-001", "employee-002")
        );
    }

    private CreateProjectRequest request(
            LocalDate startDate,
            LocalDate expectedEndDate,
            LocalDate actualEndDate,
            ProjectStatus status,
            String managerExternalId,
            List<String> memberExternalIds
    ) {
        return new CreateProjectRequest(
                "Projeto de teste",
                startDate,
                expectedEndDate,
                actualEndDate,
                new BigDecimal("250000.00"),
                "Descrição de teste",
                managerExternalId,
                status,
                memberExternalIds
        );
    }

    private Member member(String externalId, String assignment) {
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(UUID.randomUUID());
        when(member.getExternalId()).thenReturn(externalId);
        when(member.getAssignment()).thenReturn(assignment);
        return member;
    }
}
