package br.gov.endemias.dto;

import br.gov.endemias.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastroAdministrativoRequest(
    @NotNull Long agenteId,
    @NotBlank @Size(min = 8) String password,
    @NotNull UserRole role
) {}
