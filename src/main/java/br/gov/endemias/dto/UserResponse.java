package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.User;
import br.gov.endemias.domain.enums.UserRole;
import br.gov.endemias.domain.enums.UserStatus;

public record UserResponse(
    Long id,
    AgenteResponse agente,
    UserRole role,
    UserStatus status
) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
            user.getId(),
            AgenteResponse.fromEntity(user.getAgente()),
            user.getRole(),
            user.getStatus()
        );
    }
}
