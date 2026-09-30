package com.rafaeltalavera.dproject_portfolio_api.member.integration.service;

import com.rafaeltalavera.dproject_portfolio_api.member.integration.client.ExternalMemberClient;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.dto.CreateExternalMemberRequest;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.dto.ExternalMemberResponse;
import org.springframework.stereotype.Service;

@Service
public class MemberIntegrationService {

    private final ExternalMemberClient externalMemberClient;

    public MemberIntegrationService(ExternalMemberClient externalMemberClient) {
        this.externalMemberClient = externalMemberClient;
    }

    public ExternalMemberResponse findByExternalId(String externalId) {
        return externalMemberClient.findByExternalId(externalId);
    }

    public ExternalMemberResponse createExternalMember(CreateExternalMemberRequest request) {
        return externalMemberClient.create(request);
    }
}
