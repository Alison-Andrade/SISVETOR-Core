package br.gov.endemias.dto;

import br.gov.endemias.domain.entity.Area;
import jakarta.validation.constraints.NotEmpty;

public record AreaRequest(
    @NotEmpty(message = "O numero da area é obrigatorio")
    String numArea,
    Long agenteId
) {
    public Area toEntity() {
        Area area = new Area();
        area.setNumArea(this.numArea);
        return area;
    }
}
