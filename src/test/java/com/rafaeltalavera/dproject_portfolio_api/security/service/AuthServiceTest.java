package com.rafaeltalavera.dproject_portfolio_api.security.service;

import com.rafaeltalavera.dproject_portfolio_api.security.dto.LoginRequest;
import com.rafaeltalavera.dproject_portfolio_api.security.dto.LoginResponse;
import com.rafaeltalavera.dproject_portfolio_api.security.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldReturnBearerTokenWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("portfolio.admin", "password");
        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
                "portfolio.admin", null, java.util.List.of()
        );
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenService.generateToken("portfolio.admin")).thenReturn("signed-token");
        when(jwtTokenService.getExpirationInSeconds()).thenReturn(3600L);

        LoginResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("signed-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600L);
    }

    @Test
    void shouldRejectInvalidCredentials() {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("invalid"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("portfolio.admin", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Credenciais inválidas.");
    }

    @Test
    void shouldAcceptTheDemoUserPasswordHash() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        assertThat(passwordEncoder.matches(
                "password",
                "$2a$10$c7GeplQE7uUOwsUSELK66upK.StbY7zbhveup8p3Aeb/wlbMSCZu."
        )).isTrue();
    }
}
