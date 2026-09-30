package com.rafaeltalavera.dproject_portfolio_api.member.integration.client;

import com.rafaeltalavera.dproject_portfolio_api.member.integration.dto.CreateExternalMemberRequest;
import com.rafaeltalavera.dproject_portfolio_api.member.integration.dto.ExternalMemberResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

class RestClientExternalMemberClientTest {

    private MockRestServiceServer server;
    private RestClientExternalMemberClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://external-members.test");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new RestClientExternalMemberClient(builder.build());
    }

    @Test
    void shouldFindMemberByExternalId() {
        server.expect(requestTo("http://external-members.test/api/v1/members/employee-001"))
                .andExpect(method(GET))
                .andRespond(withSuccess(
                        "{\"id\":\"employee-001\",\"name\":\"Ana Silva\",\"assignment\":\"funcionário\"}",
                        MediaType.APPLICATION_JSON
                ));

        ExternalMemberResponse response = client.findByExternalId("employee-001");

        assertThat(response.id()).isEqualTo("employee-001");
        assertThat(response.name()).isEqualTo("Ana Silva");
        assertThat(response.assignment()).isEqualTo("funcionário");
        server.verify();
    }

    @Test
    void shouldCreateMemberUsingExternalApi() {
        server.expect(requestTo("http://external-members.test/api/v1/members"))
                .andExpect(method(POST))
                .andExpect(content().json("{\"name\":\"Ana Silva\",\"assignment\":\"funcionário\"}"))
                .andRespond(withSuccess(
                        "{\"id\":\"employee-002\",\"name\":\"Ana Silva\",\"assignment\":\"funcionário\"}",
                        MediaType.APPLICATION_JSON
                ));

        ExternalMemberResponse response = client.create(
                new CreateExternalMemberRequest("Ana Silva", "funcionário")
        );

        assertThat(response.id()).isEqualTo("employee-002");
        assertThat(response.assignment()).isEqualTo("funcionário");
        server.verify();
    }

    @Test
    void shouldRetrieveAMemberAfterCreatingIt() {
        String externalId = "employee-created-001";
        String responseBody = "{\"id\":\"employee-created-001\",\"name\":\"Marina Souza\",\"assignment\":\"funcionário\"}";
        server.expect(requestTo("http://external-members.test/api/v1/members"))
                .andExpect(method(POST))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
        server.expect(requestTo("http://external-members.test/api/v1/members/" + externalId))
                .andExpect(method(GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        ExternalMemberResponse created = client.create(new CreateExternalMemberRequest("Marina Souza", "funcionário"));
        ExternalMemberResponse retrieved = client.findByExternalId(created.id());

        assertThat(retrieved.id()).isEqualTo(created.id());
        assertThat(retrieved.assignment()).isEqualTo("funcionário");
        server.verify();
    }

    @Test
    void shouldReportExternalMemberNotFound() {
        server.expect(requestTo("http://external-members.test/api/v1/members/unknown-member"))
                .andExpect(method(GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.findByExternalId("unknown-member"))
                .isInstanceOf(com.rafaeltalavera.dproject_portfolio_api.member.integration.exception.ExternalMemberNotFoundException.class)
                .hasMessageContaining("unknown-member");

        server.verify();
    }
}
