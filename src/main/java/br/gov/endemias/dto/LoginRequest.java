package br.gov.endemias.dto;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
    @NotEmpty(message = "O CPF, Email ou Matricula é obrigatório")
    String username,
    @NotEmpty(message = "A senha é obrigatória")
    String password
) {
    
}
