package br.gov.endemias.config.security;

import lombok.Builder;

@Builder
public record JWTUserData(
    Long userId,
    String cpf,
    String role
) {
    
}
