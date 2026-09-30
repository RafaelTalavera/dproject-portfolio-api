package com.rafaeltalavera.dproject_portfolio_api.member.integration.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ExternalMemberClientProperties.class)
public class ExternalMemberClientConfig {

    @Bean("externalMemberRestClient")
    RestClient externalMemberRestClient(
            RestClient.Builder restClientBuilder,
            ExternalMemberClientProperties properties
    ) {
        return restClientBuilder.baseUrl(properties.baseUrl().toString()).build();
    }
}
