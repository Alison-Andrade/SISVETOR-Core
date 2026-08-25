package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.User;

public record UserResponse(
    Long id,
    AgenteResponse agente,
    String role
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
            user.getId(),
            AgenteResponse.fromEntity(user.getAgente()),
            user.getRole()
        );
    }
}
