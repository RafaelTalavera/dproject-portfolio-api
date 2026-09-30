package com.rafaeltalavera.dproject_portfolio_api.project.service;

import com.rafaeltalavera.dproject_portfolio_api.member.cache.domain.Member;
import com.rafaeltalavera.dproject_portfolio_api.member.cache.repository.MemberRepository;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.service.MemberIntegrationService;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.Project;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.CreateProjectRequest;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.ProjectQueryFilter;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.UpdateProjectRequest;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.ChangeProjectStatusRequest;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.policy.ProjectDeletionPolicy;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.policy.ProjectStatusTransitionPolicy;
import com.rafaeltalavera.dproject_portfolio_api.project.exception.ProjectBusinessRuleException;
import com.rafaeltalavera.dproject_portfolio_api.project.exception.ProjectNotFoundException;
import com.rafaeltalavera.dproject_portfolio_api.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.time.LocalDate;

@Service
public class ProjectService {

    private static final String EMPLOYEE_ASSIGNMENT = "funcionário";
    private static final int MINIMUM_MEMBERS_PER_PROJECT = 1;
    private static final int MAXIMUM_MEMBERS_PER_PROJECT = 10;
    private static final long MAXIMUM_ACTIVE_PROJECTS_PER_MEMBER = 3;

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final MemberIntegrationService memberIntegrationService;

    public ProjectService(
            ProjectRepository projectRepository,
            MemberRepository memberRepository,
            MemberIntegrationService memberIntegrationService
    ) {
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.memberIntegrationService = memberIntegrationService;
    }

    @Transactional
    public Project create(CreateProjectRequest request) {
        validateDates(request);
        validateInitialStatus(request.status());
        validateMemberCount(request.memberExternalIds());
        validateNoDuplicateMembers(request.memberExternalIds());

        Set<Member> members = resolveEmployees(request.memberExternalIds());
        Member manager = findManager(request.managerExternalId(), members);
        validateManagerIsAllocated(manager, members);
        validateActiveProjectLimit(members);

        Project project = Project.create(
                request.name(),
                request.startDate(),
                request.expectedEndDate(),
                request.actualEndDate(),
                request.totalBudget(),
                request.description(),
                manager,
                request.status(),
                members
        );
        return projectRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Page<Project> findAll(ProjectQueryFilter filter, Pageable pageable) {
        if (filter.startDateFrom() != null && filter.startDateTo() != null
                && filter.startDateFrom().isAfter(filter.startDateTo())) {
            throw new ProjectBusinessRuleException("A data inicial do filtro não pode ser posterior à data final.");
        }
        String name = filter.name() == null || filter.name().isBlank() ? null : filter.name().trim();
        return projectRepository.findByFilter(name,
                filter.status() == null ? null : filter.status().name(), filter.managerId(),
                filter.startDateFrom(), filter.startDateTo(), filter.risk() == null ? null : filter.risk().name(), pageable);
    }

    @Transactional
    public Project update(UUID projectId, UpdateProjectRequest request) {
        Project project = findProject(projectId);
        validateDates(request.startDate(), request.expectedEndDate(), request.actualEndDate());
        validateMemberCount(request.memberExternalIds());
        validateNoDuplicateMembers(request.memberExternalIds());
        Set<Member> members = resolveEmployees(request.memberExternalIds());
        Member manager = findManager(request.managerExternalId(), members);
        validateManagerIsAllocated(manager, members);
        validateActiveProjectLimit(project, members);
        project.updateDetails(request.name(), request.startDate(), request.expectedEndDate(), request.actualEndDate(),
                request.totalBudget(), request.description(), manager, members);
        return project;
    }

    @Transactional
    public Project changeStatus(UUID projectId, ChangeProjectStatusRequest request) {
        Project project = findProject(projectId);
        if (!ProjectStatusTransitionPolicy.isAllowed(project.getStatus(), request.status())) {
            throw new ProjectBusinessRuleException("A transição de status informada não é permitida.");
        }
        if (request.status() == ProjectStatus.CLOSED) {
            if (request.actualEndDate() == null) {
                throw new ProjectBusinessRuleException("A data real de término é obrigatória para encerrar o projeto.");
            }
            if (request.actualEndDate().isBefore(project.getStartDate())) {
                throw new ProjectBusinessRuleException("A data real de término deve ser igual ou posterior à data de início.");
            }
        }
        project.changeStatus(request.status(), request.actualEndDate());
        return project;
    }

    @Transactional
    public void delete(UUID projectId) {
        Project project = findProject(projectId);
        if (!ProjectDeletionPolicy.canDelete(project.getStatus())) {
            throw new ProjectBusinessRuleException("Não é permitido excluir um projeto neste status.");
        }
        projectRepository.delete(project);
    }

    private void validateDates(CreateProjectRequest request) {
        validateDates(request.startDate(), request.expectedEndDate(), request.actualEndDate());
    }

    private void validateDates(LocalDate startDate, LocalDate expectedEndDate, LocalDate actualEndDate) {
        if (expectedEndDate.isBefore(startDate)) {
            throw new ProjectBusinessRuleException(
                    "A previsão de término deve ser igual ou posterior à data de início."
            );
        }
        if (actualEndDate != null && actualEndDate.isBefore(startDate)) {
            throw new ProjectBusinessRuleException(
                    "A data real de término deve ser igual ou posterior à data de início."
            );
        }
    }

    private void validateInitialStatus(ProjectStatus status) {
        if (status != ProjectStatus.ANALYSIS) {
            throw new ProjectBusinessRuleException("Um projeto deve ser criado no status ANALYSIS.");
        }
    }

    private void validateNoDuplicateMembers(List<String> memberExternalIds) {
        if (new HashSet<>(memberExternalIds).size() != memberExternalIds.size()) {
            throw new ProjectBusinessRuleException("A lista de membros não pode conter duplicidades.");
        }
    }

    private void validateMemberCount(List<String> memberExternalIds) {
        if (memberExternalIds.size() < MINIMUM_MEMBERS_PER_PROJECT
                || memberExternalIds.size() > MAXIMUM_MEMBERS_PER_PROJECT) {
            throw new ProjectBusinessRuleException(
                    "O projeto deve possuir entre um e dez membros."
            );
        }
    }

    private Set<Member> resolveEmployees(List<String> externalIds) {
        Set<Member> members = new LinkedHashSet<>();
        for (String externalId : externalIds) {
            memberIntegrationService.findByExternalId(externalId);
            Member member = memberRepository.findByExternalId(externalId)
                    .orElseThrow(() -> new ProjectBusinessRuleException(
                            "Não foi possível sincronizar o membro informado."
                    ));
            validateEmployee(member);
            members.add(member);
        }
        return members;
    }

    private Member findManager(String externalId, Set<Member> allocatedMembers) {
        for (Member allocatedMember : allocatedMembers) {
            if (allocatedMember.getExternalId().equals(externalId)) {
                return allocatedMember;
            }
        }

        memberIntegrationService.findByExternalId(externalId);
        Member manager = memberRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ProjectBusinessRuleException(
                        "Não foi possível sincronizar o gerente informado."
                ));
        validateEmployee(manager);
        return manager;
    }

    private void validateEmployee(Member member) {
        if (!EMPLOYEE_ASSIGNMENT.equalsIgnoreCase(member.getAssignment().trim())) {
            throw new ProjectBusinessRuleException(
                    "Somente membros com atribuição funcionário podem ser associados ao projeto."
            );
        }
    }

    private void validateManagerIsAllocated(Member manager, Set<Member> members) {
        if (!members.contains(manager)) {
            throw new ProjectBusinessRuleException("O gerente deve fazer parte dos membros alocados.");
        }
    }

    private void validateActiveProjectLimit(Set<Member> members) {
        for (Member member : members) {
            long activeProjects = projectRepository.countActiveProjectsByMemberId(member.getId());
            if (activeProjects >= MAXIMUM_ACTIVE_PROJECTS_PER_MEMBER) {
                throw new ProjectBusinessRuleException(
                        "Um membro não pode estar alocado em mais de três projetos ativos."
                );
            }
        }
    }

    private void validateActiveProjectLimit(Project project, Set<Member> members) {
        if (project.getStatus() == ProjectStatus.CLOSED || project.getStatus() == ProjectStatus.CANCELED) {
            return;
        }
        for (Member member : members) {
            if (!project.getMembers().contains(member)
                    && projectRepository.countActiveProjectsByMemberId(member.getId()) >= MAXIMUM_ACTIVE_PROJECTS_PER_MEMBER) {
                throw new ProjectBusinessRuleException("Um membro não pode estar alocado em mais de três projetos ativos.");
            }
        }
    }

    private Project findProject(UUID projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException(projectId));
    }
}
