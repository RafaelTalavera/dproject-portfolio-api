package com.rafaeltalavera.dproject_portfolio_api.report.controller;

import com.rafaeltalavera.dproject_portfolio_api.report.dto.PortfolioSummaryResponse;
import com.rafaeltalavera.dproject_portfolio_api.report.service.PortfolioReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class PortfolioReportController {

    private final PortfolioReportService portfolioReportService;

    public PortfolioReportController(PortfolioReportService portfolioReportService) {
        this.portfolioReportService = portfolioReportService;
    }

    @GetMapping("/portfolio-summary")
    public PortfolioSummaryResponse summarizePortfolio() {
        return portfolioReportService.summarize();
    }
}
