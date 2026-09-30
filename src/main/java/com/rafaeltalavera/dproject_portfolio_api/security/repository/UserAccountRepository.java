package com.rafaeltalavera.dproject_portfolio_api.security.repository;

import com.rafaeltalavera.dproject_portfolio_api.security.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByUsername(String username);
}
