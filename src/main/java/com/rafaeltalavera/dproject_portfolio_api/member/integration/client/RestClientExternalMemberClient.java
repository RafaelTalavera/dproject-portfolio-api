package com.rafaeltalavera.dproject_portfolio_api.member.integration.client;

import com.rafaeltalavera.dproject_portfolio_api.member.integration.dto.CreateExternalMemberRequest;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.dto.ExternalMemberResponse;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.exception.ExternalMemberClientException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class RestClientExternalMemberClient implements ExternalMemberClient {

    private final RestClient restClient;

    public RestClientExternalMemberClient(@Qualifier("externalMemberRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ExternalMemberResponse findByExternalId(String externalId) {
        try {
            return requireResponse(restClient.get()
                    .uri("/api/v1/members/{externalId}", externalId)
                    .retrieve()
                    .body(ExternalMemberResponse.class));
        } catch (RestClientException exception) {
            throw new ExternalMemberClientException("Não foi possível consultar a API externa de membros.", exception);
        }
    }

    @Override
    public ExternalMemberResponse create(CreateExternalMemberRequest request) {
        try {
            return requireResponse(restClient.post()
                    .uri("/api/v1/members")
                    .body(request)
                    .retrieve()
                    .body(ExternalMemberResponse.class));
        } catch (RestClientException exception) {
            throw new ExternalMemberClientException("Não foi possível criar o membro na API externa.", exception);
        }
    }

    private ExternalMemberResponse requireResponse(ExternalMemberResponse response) {
        if (response == null) {
            throw new ExternalMemberClientException("A API externa de membros retornou uma resposta vazia.");
        }
        return response;
    }
}
