package br.gov.endemias.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroPublicoRequest(
    @NotNull @Valid AgenteCadastroRequest agente,
    @NotBlank @Size(min = 8) String password
) {}
