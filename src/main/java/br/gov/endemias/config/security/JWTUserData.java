package br.gov.endemias.config.security;

import java.util.UUID;

import lombok.Builder;

@Builder
public record JWTUserData(
    Long userId,
    String cpf,
    String role,
    UUID sessionId
) {
    public JWTUserData(Long userId, String cpf, String role) {
        this(userId, cpf, role, null);
    }
}
