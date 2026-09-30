package com.rafaeltalavera.dproject_portfolio_api.member.integration.service;

import com.rafaeltalavera.dproject_portfolio_api.member.integration.client.ExternalMemberClient;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.dto.CreateExternalMemberRequest;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.dto.ExternalMemberResponse;
import com.rafaeltalavera.dproject_portfolio_api.member.cache.domain.Member;
import com.rafaeltalavera.dproject_portfolio_api.member.cache.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberIntegrationService {

    private final ExternalMemberClient externalMemberClient;
    private final MemberRepository memberRepository;

    public MemberIntegrationService(ExternalMemberClient externalMemberClient, MemberRepository memberRepository) {
        this.externalMemberClient = externalMemberClient;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ExternalMemberResponse findByExternalId(String externalId) {
        return synchronize(externalMemberClient.findByExternalId(externalId));
    }

    @Transactional
    public ExternalMemberResponse createExternalMember(CreateExternalMemberRequest request) {
        return synchronize(externalMemberClient.create(request));
    }

    private ExternalMemberResponse synchronize(ExternalMemberResponse response) {
        Member member = memberRepository.findByExternalId(response.id())
                .map(existing -> {
                    existing.updateSnapshot(response);
                    return existing;
                })
                .orElseGet(() -> Member.from(response));
        memberRepository.save(member);
        return response;
    }
}
