package com.rafaeltalavera.dproject_portfolio_api.security.service;

import com.rafaeltalavera.dproject_portfolio_api.security.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public DatabaseUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userAccountRepository.findByUsername(username)
                .map(account -> User.withUsername(account.getUsername())
                        .password(account.getPasswordHash())
                        .authorities("ROLE_" + account.getRole())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário ou senha inválidos."));
    }
}
