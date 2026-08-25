package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
    @Valid
    AgenteRequest agente,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 8, message = "A senha deve conter no mínimo 8 caracteres")
    String password,

    String role
) {
    public User toEntity() {
        User user = new User();
        user.setAgente(this.agente.toEntity());
        user.setPassword(this.password);
        user.setRole(this.role == null ? "ROLE_CAMPO" : this.role);
        return user;
    }
}
