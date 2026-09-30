package com.rafaeltalavera.dproject_portfolio_api.member.integration.service;

import com.rafaeltalavera.dproject_portfolio_api.member.cache.domain.Member;
import com.rafaeltalavera.dproject_portfolio_api.member.cache.repository.MemberRepository;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.client.ExternalMemberClient;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.dto.ExternalMemberResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberIntegrationServiceTest {

    @Mock
    private ExternalMemberClient externalMemberClient;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberIntegrationService memberIntegrationService;

    @Test
    void shouldCacheMemberReturnedByExternalApi() {
        ExternalMemberResponse response = new ExternalMemberResponse("employee-001", "Ana Silva", "funcionário");
        when(externalMemberClient.findByExternalId("employee-001")).thenReturn(response);
        when(memberRepository.findByExternalId("employee-001")).thenReturn(Optional.empty());

        memberIntegrationService.findByExternalId("employee-001");

        ArgumentCaptor<Member> memberCaptor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(memberCaptor.capture());
        assertThat(memberCaptor.getValue().getExternalId()).isEqualTo("employee-001");
        assertThat(memberCaptor.getValue().getAssignment()).isEqualTo("funcionário");
    }
}
