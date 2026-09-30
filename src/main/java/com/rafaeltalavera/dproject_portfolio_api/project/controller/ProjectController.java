package com.rafaeltalavera.dproject_portfolio_api.project.controller;

import com.rafaeltalavera.dproject_portfolio_api.member.cache.domain.Member;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.Project;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectRisk;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.ProjectStatus;
import com.rafaeltalavera.dproject_portfolio_api.project.domain.policy.ProjectRiskCalculator;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.ChangeProjectStatusRequest;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.CreateProjectRequest;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.MemberResponse;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.ProjectDetailsResponse;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.ProjectQueryFilter;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.ProjectSummaryResponse;
import com.rafaeltalavera.dproject_portfolio_api.project.dto.UpdateProjectRequest;
import com.rafaeltalavera.dproject_portfolio_api.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Projetos", description = "CRUD e ciclo de vida dos projetos do portf\u00f3lio.")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cria um projeto")
    public ResponseEntity<ProjectDetailsResponse> create(@Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDetailsResponse(service.create(request)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta o detalhe de um projeto")
    public ProjectDetailsResponse find(@PathVariable UUID id) {
        return toDetailsResponse(service.findDetailedById(id));
    }

    @GetMapping
    @Operation(summary = "Lista projetos com filtros e pagina\u00e7\u00e3o")
    public Page<ProjectSummaryResponse> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) UUID managerId,
            @RequestParam(required = false) LocalDate startDateFrom,
            @RequestParam(required = false) LocalDate startDateTo,
            @RequestParam(required = false) ProjectRisk risk,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Pageable safePageable = PageRequest.of(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 100));
        return service.findAll(new ProjectQueryFilter(name, status, managerId, startDateFrom, startDateTo, risk), safePageable)
                .map(this::toSummaryResponse);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza os dados e membros de um projeto")
    public ProjectDetailsResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateProjectRequest request) {
        return toDetailsResponse(service.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Altera explicitamente o status de um projeto")
    public ProjectSummaryResponse changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeProjectStatusRequest request
    ) {
        return toSummaryResponse(service.changeStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um projeto quando o status permite")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    private ProjectDetailsResponse toDetailsResponse(Project project) {
        return new ProjectDetailsResponse(
                project.getId(), project.getName(), project.getStartDate(), project.getExpectedEndDate(),
                project.getActualEndDate(), project.getTotalBudget(), project.getDescription(),
                toMemberResponse(project.getManager()), project.getStatus(), calculateRisk(project),
                project.getMembers().stream().map(this::toMemberResponse)
                        .sorted(Comparator.comparing(MemberResponse::externalId)).toList()
        );
    }

    private ProjectSummaryResponse toSummaryResponse(Project project) {
        return new ProjectSummaryResponse(
                project.getId(), project.getName(), project.getStartDate(), project.getExpectedEndDate(),
                project.getTotalBudget(), project.getManager().getId(), project.getStatus(), calculateRisk(project)
        );
    }

    private MemberResponse toMemberResponse(Member member) {
        return new MemberResponse(member.getId(), member.getExternalId(), member.getName(), member.getAssignment());
    }

    private ProjectRisk calculateRisk(Project project) {
        return ProjectRiskCalculator.calculate(project.getTotalBudget(), project.getStartDate(), project.getExpectedEndDate());
    }
}
