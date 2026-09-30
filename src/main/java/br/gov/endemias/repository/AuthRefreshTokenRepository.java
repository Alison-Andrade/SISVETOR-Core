package br.gov.endemias.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.endemias.domain.entity.AuthRefreshToken;

public interface AuthRefreshTokenRepository extends JpaRepository<AuthRefreshToken, String> {
}
