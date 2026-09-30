package br.gov.endemias.dto;

public record AuthResponse(
    String token,
    String tokenType,
    String refreshToken,
    long expiresIn
) {}
