package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.Area;

public record AreaResponse(
    Long id,
    String numArea,
    Long agenteId
) {
    public static AreaResponse fromEntity(Area area) {
        return new AreaResponse(
            area.getId(),
            area.getNumArea(), 
            area.getAgenteResponsavel() != null 
                ? area.getAgenteResponsavel().getId() 
                : null
        );
    }
}
