package com.rafaeltalavera.dproject_portfolio_api.security.config;

import com.rafaeltalavera.dproject_portfolio_api.common.exception.GlobalExceptionHandler;
import com.rafaeltalavera.dproject_portfolio_api.security.controller.AuthController;
import com.rafaeltalavera.dproject_portfolio_api.security.service.AuthService;
import com.rafaeltalavera.dproject_portfolio_api.security.service.DatabaseUserDetailsService;
import com.rafaeltalavera.dproject_portfolio_api.security.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtTokenService.class, GlobalExceptionHandler.class})
class SecurityFilterChainTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenService jwtTokenService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private DatabaseUserDetailsService databaseUserDetailsService;

    @Test
    void shouldRejectBusinessEndpointWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAllowRequestWithValidBearerTokenToReachTheApplication() throws Exception {
        String token = jwtTokenService.generateToken("portfolio.admin");

        mockMvc.perform(get("/api/v1/projects")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
