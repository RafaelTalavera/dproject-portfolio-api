package com.rafaeltalavera.dproject_portfolio_api.report.controller;

import com.rafaeltalavera.dproject_portfolio_api.report.dto.PortfolioSummaryResponse;
import com.rafaeltalavera.dproject_portfolio_api.report.service.PortfolioReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Relat\u00f3rios", description = "Indicadores agregados do portf\u00f3lio.")
@SecurityRequirement(name = "bearerAuth")
public class PortfolioReportController {

    private final PortfolioReportService portfolioReportService;

    public PortfolioReportController(PortfolioReportService portfolioReportService) {
        this.portfolioReportService = portfolioReportService;
    }

    @GetMapping("/portfolio-summary")
    @Operation(summary = "Consulta o resumo do portf\u00f3lio")
    public PortfolioSummaryResponse summarizePortfolio() {
        return portfolioReportService.summarize();
    }
}
