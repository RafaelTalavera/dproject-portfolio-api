package com.rafaeltalavera.dproject_portfolio_api.member.integration.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "app.external-member")
public record ExternalMemberClientProperties(@NotNull URI baseUrl) {
}
