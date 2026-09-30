package com.rafaeltalavera.dproject_portfolio_api.member.integration.client;

import com.rafaeltalavera.dproject_portfolio_api.member.integration.dto.CreateExternalMemberRequest;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.dto.ExternalMemberResponse;

public interface ExternalMemberClient {

    ExternalMemberResponse findByExternalId(String externalId);

    ExternalMemberResponse create(CreateExternalMemberRequest request);
}
