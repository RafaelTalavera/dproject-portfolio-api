package com.rafaeltalavera.dproject_portfolio_api.project.service;

import com.rafaeltalavera.dproject_portfolio_api.member.cache.domain.Member;
import com.rafaeltalavera.dproject_portfolio_api.member.cache.repository.MemberRepository;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.service.MemberIntegrationService;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.Project;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.CreateProjectRequest;
import com.rafaeltalavera.dproject_portfolio_api.project.exception.ProjectBusinessRuleException;
import com.rafaeltalavera.dproject_portfolio_api.project.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

    private void validateDates(CreateProjectRequest request) {
        if (request.expectedEndDate().isBefore(request.startDate())) {
            throw new ProjectBusinessRuleException(
                    "A previsão de término deve ser igual ou posterior à data de início."
            );
        }
        if (request.actualEndDate() != null && request.actualEndDate().isBefore(request.startDate())) {
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
}
