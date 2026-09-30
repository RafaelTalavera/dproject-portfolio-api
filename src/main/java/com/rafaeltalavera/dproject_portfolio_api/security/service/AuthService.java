package com.rafaeltalavera.dproject_portfolio_api.security.service;

import com.rafaeltalavera.dproject_portfolio_api.security.dto.LoginRequest;
import com.rafaeltalavera.dproject_portfolio_api.security.dto.LoginResponse;
import com.rafaeltalavera.dproject_portfolio_api.security.exception.InvalidCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password())
            );
            String token = jwtTokenService.generateToken(authentication.getName());
            return new LoginResponse(token, "Bearer", jwtTokenService.getExpirationInSeconds());
        } catch (BadCredentialsException exception) {
            throw new InvalidCredentialsException();
        }
    }
}
